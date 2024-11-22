package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long sellerId;
    private String shopifyOrderId;
    private String amazonMcfOrderId;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalPrice;
    private Boolean slaMet;
    private LocalDateTime deliveryEta;
}
