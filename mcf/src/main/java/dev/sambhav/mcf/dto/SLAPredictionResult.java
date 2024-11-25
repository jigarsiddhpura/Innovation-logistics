package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SLAPredictionResult {
    private Long orderId;
    private boolean willMeetSLA;
    private SLARiskLevel riskLevel;
    private long elapsedBusinessHours;
    private long remainingSLAHours;
    private LocalDateTime expectedCompletionTime;
    private List<String> recommendations;
}
