// File: src/main/java/dev/sambhav/mcf/dto/ProductRequest.java
package dev.sambhav.mcf.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class ProductRequest {

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
