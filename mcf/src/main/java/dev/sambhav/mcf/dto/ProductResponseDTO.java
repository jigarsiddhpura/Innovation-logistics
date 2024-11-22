package dev.sambhav.mcf.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
    private Long productId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
}
