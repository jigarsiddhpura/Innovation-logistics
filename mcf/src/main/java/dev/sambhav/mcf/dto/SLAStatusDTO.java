package dev.sambhav.mcf.dto;

import dev.sambhav.mcf.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAStatusDTO {
    private Long orderId;
    private OrderStatus status;
    private Boolean slaMet;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime orderUpdatedAt;
    private LocalDateTime slaDeadline;
    private Long hoursRemaining;
    private String slaStatus; // "ON_TRACK", "AT_RISK", "BREACHED"
}
