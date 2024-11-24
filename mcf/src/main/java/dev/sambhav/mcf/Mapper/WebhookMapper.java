package dev.sambhav.mcf.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sambhav.mcf.dto.OrderDto;
import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.model.OrderStatus;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public OrderDto mapToOrderDto(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);


            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(rootNode.path("id").asLong());
            orderDto.setSellerId(3L);
            orderDto.setCustomerName(rootNode.path("customer").path("first_name").asText()
                    + " " + rootNode.path("customer").path("last_name").asText());
            orderDto.setEmail(rootNode.path("email").asText());
            orderDto.setCurrentTotalPrice(new BigDecimal(rootNode.path("current_total_price").asText()));
            orderDto.setFulfillmentStatus(OrderStatus.PENDING);
            orderDto.setCreatedAt(OffsetDateTime.parse(rootNode.path("created_at").asText()).toLocalDateTime());
            orderDto.setProcessedAt(OffsetDateTime.parse(rootNode.path("processed_at").asText()).toLocalDateTime());

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