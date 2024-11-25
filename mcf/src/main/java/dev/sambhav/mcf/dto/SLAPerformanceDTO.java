package dev.sambhav.mcf.dto;

import dev.sambhav.mcf.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAPerformanceDTO {
    private long totalOrders;
    private long ordersMetSLA;
    private double slaPerformance;
    private Map<OrderStatus, Long> statusBreakdown;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
