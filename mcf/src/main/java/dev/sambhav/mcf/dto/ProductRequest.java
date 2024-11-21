// File: src/main/java/dev/sambhav/mcf/dto/ProductRequest.java
package dev.sambhav.mcf.dto;

import java.math.BigDecimal;

public class ProductRequest {

    private Long sellerId;
    private String shopifyProductId;
    private String amazonMcfSku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
    private Integer reorderThreshold;

    public Long getSellerId() {
        return sellerId;
    }

    public String getShopifyProductId() {
        return shopifyProductId;
    }

    public String getAmazonMcfSku() {
        return amazonMcfSku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getInventoryLevel() {
        return inventoryLevel;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

}
