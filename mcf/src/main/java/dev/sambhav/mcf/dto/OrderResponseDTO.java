package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private String customerName;
    private String email;
    private BigDecimal currentTotalPrice;
    private String fulfillmentStatus;
    private Boolean slaMet;
    private LocalDateTime deliveryEta;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}

