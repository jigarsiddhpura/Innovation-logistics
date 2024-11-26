// File: src/main/java/dev/sambhav/mcf/dto/ProductRequestDTO.java
package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {
    private long sellerId;
    private Long productId;
    private String title;
    private String productType;
    private String vendor;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
    private String amazonMcfSku;
    private String storeUrl;
    private StoreType storeType;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }
}
