package dev.sambhav.mcf.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "amazon_mcf_order_id")
    private String amazonMcfOrderId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "email")
    private String email;

    @Column(name = "current_total_price", precision = 10, scale = 2)
    private BigDecimal currentTotalPrice;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING) // Maps Java enum to database enum type
    @Column(name = "fulfillment_status", columnDefinition = "order_status DEFAULT 'PENDING'")
    private OrderStatus fulfillmentStatus;

    @Column(name = "sla_met")
    private Boolean slaMet;

    @Column(name = "delivery_eta")
    private LocalDateTime deliveryEta;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (fulfillmentStatus == null) {
            fulfillmentStatus = OrderStatus.PENDING; // Default status set
        }
        createdAt = LocalDateTime.now();
        processedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        processedAt = LocalDateTime.now();
    }
}
