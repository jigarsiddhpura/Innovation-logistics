// File: src/main/java/dev/sambhav/mcf/dto/ProductRequestDTO.java
package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {
    private Long productId;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
