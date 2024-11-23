package dev.sambhav.mcf.Mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sambhav.mcf.dto.OrderDto;
import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class WebhookMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductDTO mapToProductDto(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);

            ProductDTO productDto = new ProductDTO();
            productDto.setProductId(rootNode.path("id").asLong());
            productDto.setTitle(rootNode.path("title").asText());
            productDto.setProductType(rootNode.path("product_type").asText());
            productDto.setVendor(rootNode.path("vendor").asText());
            productDto.setDescription(rootNode.path("body_html").asText());
            productDto.setPrice(new BigDecimal(rootNode.path("variants").get(0).path("price").asText())); // First variant price
            productDto.setInventoryLevel(rootNode.path("variants").get(0).path("inventory_quantity").asInt());
            productDto.setPublishedAt(LocalDateTime.parse(rootNode.path("published_at").asText()));
            productDto.setUpdatedAt(LocalDateTime.parse(rootNode.path("updated_at").asText()));

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
            orderDto.setSellerId(rootNode.path("app_id").asLong());
            orderDto.setCustomerName(rootNode.path("customer").path("first_name").asText()
                    + " " + rootNode.path("customer").path("last_name").asText());
            orderDto.setEmail(rootNode.path("email").asText());
            orderDto.setCurrentTotalPrice(new BigDecimal(rootNode.path("current_total_price").asText()));
            orderDto.setFulfillmentStatus(OrderStatus.valueOf(rootNode.path("fulfillment_status").asText().toUpperCase()));
            orderDto.setCreatedAt(LocalDateTime.parse(rootNode.path("created_at").asText()));
            orderDto.setProcessedAt(LocalDateTime.parse(rootNode.path("processed_at").asText()));

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