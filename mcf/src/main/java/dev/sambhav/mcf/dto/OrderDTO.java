package dev.sambhav.mcf.dto;

import dev.sambhav.mcf.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long orderId;
    private Long sellerId;
    private String orderName;
    private String amazonMcfOrderId;
    private String customerName;
    private String email;
    private BigDecimal currentTotalPrice;
    private OrderStatus fulfillmentStatus;
    private Boolean slaMet;
    private LocalDateTime deliveryEta;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
