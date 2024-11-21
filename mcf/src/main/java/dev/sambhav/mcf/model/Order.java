package dev.sambhav.mcf.model;

// 🔴 use jakarta.persistence instead of javax, bcz it is modern 🔴

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
// import org.springframework.data.annotation.Id;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "shopify_order_id")
    private String shopifyOrderId;

    @Column(name = "amazon_mcf_order_id")
    private String amazonMcfOrderId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "sla_met")
    private Boolean slaMet;

    @Column(name = "delivery_eta")
    private LocalDateTime deliveryEta;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Order() {
    }

    @PrePersist
    protected void onCreate() {
        // This method runs BEFORE the record is first inserted into database
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // This method runs BEFORE the record is updated in the database
        updatedAt = LocalDateTime.now();
    }
}
