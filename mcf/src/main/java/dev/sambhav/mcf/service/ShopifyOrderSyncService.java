package dev.sambhav.mcf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.dto.StoreType;
import dev.sambhav.mcf.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShopifyOrderSyncService {

    @Value("${shopify.access.token}")
    private String shopifyAccessToken;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Order> syncShopifyOrders(String storeUrl) {
        try {
            // Remove any https:// or http:// if present
            storeUrl = storeUrl.replaceAll("^(https?://)", "");

            // Construct the correct API URL for orders
            String shopifyApiUrl = String.format("https://%s/admin/api/2024-01/orders.json?status=any", storeUrl);
            log.info("Calling Shopify Orders API: {}", shopifyApiUrl);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", shopifyAccessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make request
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Making request to Shopify with token: {}", shopifyAccessToken.substring(0, 5) + "...");

            ResponseEntity<String> response = restTemplate.exchange(
                    shopifyApiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode orders = root.get("orders");

                List<Order> savedOrders = new ArrayList<>();

                if (orders != null && orders.isArray()) {
                    for (JsonNode orderNode : orders) {
                        try {
                            Order order = mapShopifyOrderToEntity(orderNode, storeUrl);
                            savedOrders.add(orderRepository.save(order));
                            log.info("Saved order: {}", order.getOrderId());
                        } catch (Exception e) {
                            log.error("Error processing order: {}", orderNode.get("order_number"), e);
                        }
                    }

                    log.info("Successfully synced {} orders from Shopify", savedOrders.size());
                } else {
                    log.warn("No orders found in the response");
                }

                return savedOrders;
            } else {
                log.error("Failed to fetch orders from Shopify. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to fetch orders from Shopify: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error syncing orders from Shopify: {}", e.getMessage(), e);
            throw new RuntimeException("Error syncing orders from Shopify: " + e.getMessage(), e);
        }
    }

    private Order mapShopifyOrderToEntity(JsonNode orderNode, String storeUrl) {
        Order order = new Order();

        // Map basic order information
        order.setOrderId(orderNode.get("order_number").asLong());
        order.setOrderName("#" + orderNode.get("order_number").asText());

        // Customer information
        JsonNode customer = orderNode.get("customer");
        if (customer != null && !customer.isNull()) {
            String firstName = customer.get("first_name").asText("");
            String lastName = customer.get("last_name").asText("");
            order.setCustomerName(firstName + " " + lastName);
            order.setEmail(customer.get("email").asText());
        }

        // Price information
        order.setCurrentTotalPrice(new BigDecimal(orderNode.get("total_price").asText()));

        // Fulfillment status mapping
        String fulfillmentStatus = orderNode.get("fulfillment_status").asText("null");
        switch (fulfillmentStatus) {
            case "fulfilled" -> order.setFulfillmentStatus(OrderStatus.DELIVERED);
            case "partial" -> order.setFulfillmentStatus(OrderStatus.IN_PROGRESS);
            case "null" -> order.setFulfillmentStatus(OrderStatus.PENDING);
            default -> order.setFulfillmentStatus(OrderStatus.PENDING);
        }

        // Store information
        order.setStoreUrl("https://"+storeUrl);
        order.setStoreType(StoreType.SHOPIFY);

        // Timestamps
        if (orderNode.has("created_at") && !orderNode.get("created_at").isNull()) {
            order.setCreatedAt(
                    OffsetDateTime.parse(orderNode.get("created_at").asText())
                            .toLocalDateTime()
            );
        } else {
            order.setCreatedAt(LocalDateTime.now());
        }

        if (orderNode.has("processed_at") && !orderNode.get("processed_at").isNull()) {
            order.setProcessedAt(
                    OffsetDateTime.parse(orderNode.get("processed_at").asText())
                            .toLocalDateTime()
            );
        }

        // Set estimated delivery date (e.g., 3 days from created date)
        order.setDeliveryEta(order.getCreatedAt().plusDays(3));

        // Set SLA met based on fulfillment status
        order.setSlaMet(OrderStatus.DELIVERED.equals(order.getFulfillmentStatus()));

        return order;
    }

    public void deleteAllOrders(String storeUrl) {
        orderRepository.deleteByStoreUrl(storeUrl);
    }
}
