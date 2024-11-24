package dev.sambhav.mcf.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sambhav.mcf.dto.OrderDTO;
import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.model.OrderStatus;
import jakarta.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class WebhookMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private HttpSession session;

    public ProductDTO mapToProductDto(String payload, String platform) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            ProductDTO productDto = new ProductDTO();

            if ("shopify".equalsIgnoreCase(platform)) {

                productDto.setProductId(rootNode.path("id").asLong());
                productDto.setTitle(rootNode.path("title").asText());
                productDto.setProductType(rootNode.path("product_type").asText());
                productDto.setVendor(rootNode.path("vendor").asText());
                productDto.setDescription(rootNode.path("body_html").asText());
                productDto.setPrice(new BigDecimal(rootNode.path("variants").get(0).path("price").asText())); // First variant price
                productDto.setInventoryLevel(rootNode.path("variants").get(0).path("inventory_quantity").asInt());
                productDto.setPublishedAt(OffsetDateTime.parse(rootNode.path("published_at").asText()).toLocalDateTime());
                productDto.setUpdatedAt(OffsetDateTime.parse(rootNode.path("updated_at").asText()).toLocalDateTime());
            } else if ("dukaan".equalsIgnoreCase(platform)) {
                productDto.setProductId(rootNode.path("id").asLong());
                productDto.setTitle(rootNode.path("title").asText());
                productDto.setProductType("customer");  // Since this is a customer record
                productDto.setVendor(rootNode.path("name").asText());  // Using customer name as vendor
                productDto.setDescription(rootNode.path("body_html").asText());  // Using email as description
                productDto.setPrice(new BigDecimal(rootNode.path("variants").get(0).path("price").asInt()));  // Default price since customer payload doesn't have price
                productDto.setInventoryLevel(rootNode.path("variants").get(0).path("inventory_quantity").asInt());  // Default inventory since customer payload doesn't have inventory

                // String createdAtDateTimeStr = rootNode.path("created_at").asText().trim();
                // String updatedAtDateTimeStr = rootNode.path("updated_at").asText().trim();
                // System.out.println("Created At: " + createdAtDateTimeStr);
                // System.out.println("Updated At: " + updatedAtDateTimeStr);

                productDto.setPublishedAt(LocalDateTime.now());
                productDto.setUpdatedAt(LocalDateTime.now());

            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            return productDto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping payload to ProductDto", e);
        }
    }

    public Long mapToProductId(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            return rootNode.path("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping payload to Product ID", e);
        }
    }

    public OrderDTO mapToOrderDto(String payload, String platform) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            OrderDTO orderDto = new OrderDTO();

            if ("shopify".equalsIgnoreCase(platform)) {
                orderDto.setOrderId(rootNode.path("id").asLong());
                orderDto.setSellerId(1L);
                orderDto.setOrderName(rootNode.path("line_items").get(0).path("name").asText());
                log.info(rootNode.path("line_items").get(0).path("name").asText());
                orderDto.setAmazonMcfOrderId(rootNode.path("amazon_mcf_order_id").asText());
                orderDto.setCustomerName(rootNode.path("customer").path("first_name").asText());
                orderDto.setEmail(rootNode.path("customer").path("email").asText());
                orderDto.setCurrentTotalPrice(new BigDecimal(rootNode.path("total_price").asText()));
                orderDto.setFulfillmentStatus(OrderStatus.PENDING);
                orderDto.setSlaMet(rootNode.path("sla_met").asBoolean());
                orderDto.setDeliveryEta(LocalDateTime.now());
                orderDto.setCreatedAt(LocalDateTime.now());
                orderDto.setProcessedAt(LocalDateTime.now());
            } else if ("dukaan".equalsIgnoreCase(platform)) {
                orderDto.setOrderId(rootNode.path("uuid").asLong());
                orderDto.setSellerId(1L);
                orderDto.setAmazonMcfOrderId(null);
//                orderDto.setOrderName(rootNode.path("line_items").get(0).path("title").asText());
//                log.info(rootNode.path("line_items").get(0).path("name").asText());// Dukaan payload likely lacks this
                orderDto.setCustomerName(rootNode.path("customer_name").asText());
                orderDto.setEmail(rootNode.path("email").asText());
                orderDto.setCurrentTotalPrice(new BigDecimal(rootNode.path("total_price").asDouble()));
                orderDto.setFulfillmentStatus(OrderStatus.PENDING); // Default to pending for Dukaan
                orderDto.setSlaMet(false); // Assuming SLA Met not available in Dukaan payload
                orderDto.setDeliveryEta(LocalDateTime.now()); // Dukaan payload may lack this field
                orderDto.setCreatedAt(LocalDateTime.now());
                orderDto.setProcessedAt(LocalDateTime.now());
            } else {
                throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            return orderDto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping payload to OrderDto", e);
        }
    }
    public Long mapToOrderId(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            return rootNode.path("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping payload to Order ID", e);
        }
    }
}