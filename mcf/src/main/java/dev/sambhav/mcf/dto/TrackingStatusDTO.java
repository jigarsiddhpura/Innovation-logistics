package dev.sambhav.mcf.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
public class TrackingStatusDTO {
    private String currentStatus;
    private TrackingMilestone[] milestones;
    
    @Data
    public static class TrackingMilestone {
        private String status;
        @JsonFormat(pattern = "EEE, dd MMM")
        private LocalDateTime date;
        private boolean isCompleted;
        private String displayText;
    }
}
