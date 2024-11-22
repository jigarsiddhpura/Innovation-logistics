// File: src/main/java/dev/sambhav/mcf/dto/ProductRequestDTO.java
package dev.sambhav.mcf.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ProductRequestDTO {
    private Long sellerId;
    private String shopifyProductId;
    private String amazonMcfSku;
    private String name;
    private String title;
    private String productType;
    private String vendor;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
    private Integer reorderThreshold;
    private Timestamp publishedAt;
    private Timestamp updatedAt;
}
