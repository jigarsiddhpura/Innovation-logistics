// File: src/main/java/dev/sambhav/mcf/model/Product.java
package dev.sambhav.mcf.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data // pretty much the same as @Getter and @Setter
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(optional = false)
    // Many Orders → One Seller
    @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    // name -> name of the FK column in orders table, referencedColumnName -> name of the PK column in sellers table
    private Seller seller;

    @Column(name = "shopify_product_id", unique = true)
    private String shopifyProductId;

    @Column(name = "amazon_mcf_sku", unique = true)
    private String amazonMcfSku;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "inventory_level")
    private Integer inventoryLevel;

    @Column(name = "reorder_threshold", columnDefinition = "INT DEFAULT 0")
    private Integer reorderThreshold;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Product() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Setter Methods
    // public void setProductId(Long productId) {
    //     this.productId = productId;
    // }

    // public void setSeller(Seller seller) {
    //     this.seller = seller;
    // }

    // public void setShopifyProductId(String shopifyProductId) {
    //     this.shopifyProductId = shopifyProductId;
    // }

    // public void setAmazonMcfSku(String amazonMcfSku) {
    //     this.amazonMcfSku = amazonMcfSku;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public void setDescription(String description) {
    //     this.description = description;
    // }

    // public void setPrice(BigDecimal price) {
    //     this.price = price;
    // }

    // public void setInventoryLevel(Integer inventoryLevel) {
    //     this.inventoryLevel = inventoryLevel;
    // }

    // public void setReorderThreshold(Integer reorderThreshold) {
    //     this.reorderThreshold = reorderThreshold;
    // }

    // // Created and Updated timestamps are managed by JPA annotations
    // // but adding setters for completeness
    // public void setCreatedAt(LocalDateTime createdAt) {
    //     this.createdAt = createdAt;
    // }

    // public void setUpdatedAt(LocalDateTime updatedAt) {
    //     this.updatedAt = updatedAt;
    // }
}
