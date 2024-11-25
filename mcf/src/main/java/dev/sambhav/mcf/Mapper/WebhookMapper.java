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
import java.util.Map;

@Slf4j
@Component
public class WebhookMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private HttpSession session;

    public ProductDTO mapToProductDto(String payload, String platform) {
        try {
            log.info("Payload: {}", payload);
            JsonNode rootNode = objectMapper.readTree(payload);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

            ProductDTO productDto = new ProductDTO();

            if ("shopify".equalsIgnoreCase(platform)) {
                productDto.setProductId(rootNode.path("id").asLong());
                productDto.setTitle(rootNode.path("title").asText());
                productDto.setProductType(rootNode.path("product_type").asText());
                productDto.setVendor(rootNode.path("vendor").asText());
                productDto.setDescription(rootNode.path("body_html").asText());
                productDto.setPrice(new BigDecimal(rootNode.path("variants").get(0).path("price").asText()));
                productDto.setInventoryLevel(rootNode.path("variants").get(0).path("inventory_quantity").asInt());
                productDto.setPublishedAt(OffsetDateTime.parse(rootNode.path("published_at").asText()).toLocalDateTime());
                productDto.setUpdatedAt(OffsetDateTime.parse(rootNode.path("updated_at").asText()).toLocalDateTime());
            } else if ("dukaan".equalsIgnoreCase(platform)) {
                productDto.setProductId(rootNode.findPath("id").asLong());
                productDto.setTitle(rootNode.findPath("title").asText());
                productDto.setProductType(rootNode.findPath("product_type").asText());  // Since this is a customer record
                productDto.setVendor(rootNode.findPath("vendor").asText());  // Using customer name as vendor
                productDto.setDescription(rootNode.findPath("body_html").asText());  // Using email as description
                productDto.setPrice(new BigDecimal(rootNode.findPath("variants").get(0).findPath("price").asInt()));  // Default price since customer payload doesn't have price
                productDto.setInventoryLevel(rootNode.findPath("variants").get(0).findPath("inventory_quantity").asInt());  // Default inventory since customer payload doesn't have inventory
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
                String orderIdStr = rootNode.findPath("id").asText();
                orderDto.setOrderId(Long.parseLong(orderIdStr));
                orderDto.setSellerId(1L);
                orderDto.setAmazonMcfOrderId(null);
                orderDto.setOrderName(rootNode.findPath("line_items").get(0).findPath("title").asText());
                orderDto.setCustomerName(rootNode.findPath("shipping_address").findPath("full_name").asText());
                orderDto.setEmail(rootNode.findPath("email").asText());
                orderDto.setCurrentTotalPrice(new BigDecimal(rootNode.findPath("total_price").asDouble()));
                orderDto.setFulfillmentStatus(OrderStatus.PENDING);
                orderDto.setSlaMet(true);
                orderDto.setDeliveryEta(LocalDateTime.now());
                orderDto.setCreatedAt(LocalDateTime.now());
                orderDto.setProcessedAt(LocalDateTime.now());
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