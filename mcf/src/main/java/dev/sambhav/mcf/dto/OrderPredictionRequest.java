package dev.sambhav.mcf.dto;

import dev.sambhav.mcf.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderPredictionRequest {
    private Long orderId;
    private LocalDateTime orderCreatedTime;
    private OrderStatus currentStatus;
    private LocalDateTime lastUpdateTime;
}
