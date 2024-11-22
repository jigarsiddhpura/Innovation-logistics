package dev.sambhav.mcf.service;

import dev.sambhav.mcf.dto.SellerResponseDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SellerService sellerService; // Renamed for clarity

    @Transactional
    public Order saveOrderToMcf(Order order) {
        // Validating seller existence
        SellerResponseDTO seller = getSellerById(order.getSellerId());

        // Ensuring default status
        if (order.getFulfillmentStatus() == null) {
            order.setFulfillmentStatus(OrderStatus.PENDING);
        }

        return orderRepository.save(order); // Saving the order
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Fetch all orders
    }

    public OrderStatus track(Long orderId) {
        return orderRepository.getOrderStatus(orderId); // Delegate repository logic
    }

    @Transactional
    public Order fulfillOrder(Long orderId) {
        // Fetching and validating the order
        Order order = getOrderById(orderId);

        // Ensuring the order is in the correct state
        if (order.getFulfillmentStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is already fulfilled or in progress.");
        }

        // Updating the status
        order.setFulfillmentStatus(OrderStatus.IN_PROGRESS);

        return orderRepository.save(order); // Saving the updated order
    }

    // Helper method to fetch an order by ID
    private Order getOrderById(Long orderId) {
        // TODO: FIND BY ORDERID wasn't working
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    // Helper method to fetch a seller by ID
    private SellerResponseDTO getSellerById(Long sellerId) {
        return sellerService.getSellerById(sellerId);
    }

    @Transactional
    public void saveOrderFromWebhook(String payload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(payload);

        // Extract order details
        Order order = new Order();
        order.setOrderId(rootNode.get("order_number").asLong());
        order.setSellerId(rootNode.has("seller_id") ? rootNode.get("seller_id").asLong() : 1); // Handle optional field
        order.setCustomerName(rootNode.get("customer").get("first_name").asText() + " " + rootNode.get("customer").get("last_name").asText());
        order.setEmail(rootNode.has("contact_email") ? rootNode.get("contact_email").asText() : null);
        order.setCurrentTotalPrice(new BigDecimal(rootNode.get("current_total_price").asText()));
        order.setFulfillmentStatus(OrderStatus.PENDING);

        // Handle boolean field
        order.setSlaMet(rootNode.has("sla_met") && !rootNode.get("sla_met").isNull() ? rootNode.get("sla_met").asBoolean() : null);

        // Parse timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        if (rootNode.has("created_at") && !rootNode.get("created_at").isNull()) {
            order.setCreatedAt(LocalDateTime.parse(rootNode.get("created_at").asText(), formatter));
        }
        if (rootNode.has("processed_at") && !rootNode.get("processed_at").isNull()) {
            order.setProcessedAt(OffsetDateTime.parse(rootNode.get("processed_at").asText(), formatter).toLocalDateTime());
        }
        if (rootNode.has("delivery_eta") && !rootNode.get("delivery_eta").isNull()) {
            order.setDeliveryEta(OffsetDateTime.parse(rootNode.get("delivery_eta").asText(), formatter).toLocalDateTime());
        }

        // Save the order to the database
        orderRepository.save(order);
    }
}
    




