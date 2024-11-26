package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long orderId;
    private Long sellerId;
    private String customerName;
    private String email;
    private BigDecimal currentTotalPrice;
    private String fulfillmentStatus;
    private Boolean slaMet;
    private String deliveryEta;
    private String createdAt;
    private String processedAt;
    private String storeUrl;
    private StoreType storeType;
}

