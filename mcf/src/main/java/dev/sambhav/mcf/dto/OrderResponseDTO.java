package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private String shopifyOrderId;
    private String amazonMcfOrderId;
    private String customerName;
    private String status;
    private LocalDateTime updatedAt;


    // Getters
    public Long getOrderId() { return orderId; }
    public String getShopifyOrderId() { return shopifyOrderId; }
    public String getAmazonMcfOrderId() { return amazonMcfOrderId; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

