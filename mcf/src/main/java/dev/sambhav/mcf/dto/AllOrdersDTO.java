package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AllOrdersDTO {
    private Long orderId;
    private String status; // Enum as String
    private BigDecimal totalPrice;
}
