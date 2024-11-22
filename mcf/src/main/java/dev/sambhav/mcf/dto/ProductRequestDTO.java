package dev.sambhav.mcf.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    private Long sellerId;
    private String shopifyProductId;
    private String amazonMcfSku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
    private Integer reorderThreshold;
}
