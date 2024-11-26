// File: src/main/java/dev/sambhav/mcf/model/Product.java
package dev.sambhav.mcf.model;

import dev.sambhav.mcf.dto.StoreType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data // pretty much the same as @Getter and @Setter
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "title")
    private String title;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "vendor")
    private String vendor;

    // @ManyToOne(optional = false)
    // // Many Orders → One Seller
    // @JoinColumn(name = "seller_id", referencedColumnName = "seller_id")
    // // name -> name of the FK column in orders table, referencedColumnName -> name of the PK column in sellers table
    // private Seller seller;

    // @Column(name = "shopify_product_id", unique = true)
    // private String shopifyProductId;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "inventory_level")
    private Integer inventoryLevel;

    @Column(name = "amazon_mcf_sku", unique = true)
    private String amazonMcfSku;

    // @Column(name = "reorder_threshold", columnDefinition = "INT DEFAULT 0")
    // private Integer reorderThreshold;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "store_url")
    private String storeUrl;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "store_type")
    private StoreType storeType;

    // Constructors
    public Product() {}

    @PrePersist
    protected void onCreate() {
        publishedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    
}
