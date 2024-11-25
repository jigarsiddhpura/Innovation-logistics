package dev.sambhav.mcf.service;

import dev.sambhav.mcf.dto.OrderPredictionRequest;  // Updated import
import dev.sambhav.mcf.dto.SLAPredictionResult;     // Updated import
import dev.sambhav.mcf.dto.SLARiskLevel;           // Updated import
import dev.sambhav.mcf.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SLAService {
    // Standard order processing SLA times in hours (from original OrderService)
    private static final int PROCESSING_SLA_HOURS = 24;  // 24 hours for processing
    private static final int SHIPPING_SLA_HOURS = 72;    // 72 hours for shipping
    private static final int DELIVERY_SLA_HOURS = 120;   // 120 hours (5 days) total delivery time

    /**
     * Predicts if an order will meet SLA based on its timestamps and status
     */
    public SLAPredictionResult predictSLAStatus(OrderPredictionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        OrderStatus currentStatus = request.getCurrentStatus();

        // Special handling for DELIVERED orders
        if (currentStatus == OrderStatus.DELIVERED) {
            return handleDeliveredOrder(request);
        }

        // Regular flow for other statuses
        LocalDateTime orderCreatedTime = request.getOrderCreatedTime();
        LocalDateTime lastUpdateTime = request.getLastUpdateTime();

        long elapsedBusinessHours = calculateBusinessHours(orderCreatedTime, now);
        long remainingSLAHours = calculateRemainingSLAHours(currentStatus, elapsedBusinessHours);
        LocalDateTime expectedCompletionTime = calculateExpectedCompletion(now, remainingSLAHours);
        boolean willMeetSLA = determineIfWillMeetSLA(currentStatus, elapsedBusinessHours, lastUpdateTime);
        SLARiskLevel riskLevel = calculateRiskLevel(willMeetSLA, remainingSLAHours);

        return SLAPredictionResult.builder()
                .orderId(request.getOrderId())
                .willMeetSLA(willMeetSLA)
                .riskLevel(riskLevel)
                .elapsedBusinessHours(elapsedBusinessHours)
                .remainingSLAHours(remainingSLAHours)
                .expectedCompletionTime(expectedCompletionTime)
                .recommendations(generateRecommendations(riskLevel, currentStatus))
                .build();
    }

    private SLAPredictionResult handleDeliveredOrder(OrderPredictionRequest request) {
        LocalDateTime orderCreatedTime = request.getOrderCreatedTime();
        LocalDateTime deliveryTime = request.getLastUpdateTime();

        if (deliveryTime == null) {
            deliveryTime = LocalDateTime.now();
        }

        long totalDeliveryHours = calculateBusinessHours(orderCreatedTime, deliveryTime);
        boolean metSLA = totalDeliveryHours <= DELIVERY_SLA_HOURS;

        return SLAPredictionResult.builder()
                .orderId(request.getOrderId())
                .willMeetSLA(metSLA)
                .riskLevel(metSLA ? SLARiskLevel.LOW : SLARiskLevel.HIGH)
                .elapsedBusinessHours(totalDeliveryHours)
                .remainingSLAHours(0)  // No remaining hours for delivered orders
                .expectedCompletionTime(deliveryTime)  // Actual completion time
                .recommendations(generateDeliveredOrderRecommendations(metSLA))
                .build();
    }

    private List<String> generateDeliveredOrderRecommendations(boolean metSLA) {
        List<String> recommendations = new ArrayList<>();
        if (metSLA) {
            recommendations.add("Order delivered within SLA");
            recommendations.add("No action required");
        } else {
            recommendations.add("Order delivered outside SLA");
            recommendations.add("Review delivery process for improvements");
            recommendations.add("Consider customer compensation if applicable");
        }
        return recommendations;
    }

    private List<String> generateRecommendations(SLARiskLevel riskLevel, OrderStatus status) {
        List<String> recommendations = new ArrayList<>();

        // Don't generate recommendations for DELIVERED status
        if (status == OrderStatus.DELIVERED) {
            return generateDeliveredOrderRecommendations(true);
        }

        switch (riskLevel) {
            case HIGH -> {
                recommendations.add("Immediate attention required");
                recommendations.add("Escalate to supervisor");
                if (status == OrderStatus.SHIPPED) {
                    recommendations.add("Contact shipping partner for express delivery");
                }
            }
            case MEDIUM -> {
                recommendations.add("Monitor closely");
                recommendations.add("Prepare contingency plan");
                recommendations.add("Consider expedited processing");
            }
            case LOW -> {
                recommendations.add("Continue standard processing");
                recommendations.add("Regular monitoring sufficient");
            }
        }

        return recommendations;
    }

    private boolean determineIfWillMeetSLA(OrderStatus status, long elapsedHours, LocalDateTime lastUpdateTime) {
        return switch (status) {
            case PENDING -> elapsedHours <= PROCESSING_SLA_HOURS;
            case IN_PROGRESS -> elapsedHours <= PROCESSING_SLA_HOURS;
            case SHIPPED -> elapsedHours <= SHIPPING_SLA_HOURS;
            case DELIVERED -> true;  // Handled separately in handleDeliveredOrder
            case CANCELLED -> false;
        };
    }

    private long calculateBusinessHours(LocalDateTime start, LocalDateTime end) {
        long totalHours = 0;
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            if (isBusinessHour(current)) {
                totalHours++;
            }
            current = current.plusHours(1);
        }

        return totalHours;
    }

    private boolean isBusinessHour(LocalDateTime time) {
        // Check if it's a business day (Monday-Friday)
        if (time.getDayOfWeek() == DayOfWeek.SATURDAY || time.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        // Check if it's during business hours (9 AM - 6 PM)
        int hour = time.getHour();
        return hour >= 9 && hour < 18;
    }

    private long calculateRemainingSLAHours(OrderStatus status, long elapsedHours) {
        return switch (status) {
            case PENDING -> PROCESSING_SLA_HOURS - elapsedHours;
            case IN_PROGRESS -> PROCESSING_SLA_HOURS - elapsedHours;
            case SHIPPED -> SHIPPING_SLA_HOURS - elapsedHours;
            case DELIVERED -> 0;
            default -> 0;
        };
    }

    private SLARiskLevel calculateRiskLevel(boolean willMeetSLA, long remainingHours) {
        if (!willMeetSLA) {
            return SLARiskLevel.HIGH;
        }

        if (remainingHours <= 4) {
            return SLARiskLevel.HIGH;
        } else if (remainingHours <= 8) {
            return SLARiskLevel.MEDIUM;
        } else {
            return SLARiskLevel.LOW;
        }
    }

    private LocalDateTime calculateExpectedCompletion(LocalDateTime now, long remainingHours) {
        LocalDateTime expectedTime = now;
        long hoursAdded = 0;

        while (hoursAdded < remainingHours) {
            expectedTime = expectedTime.plusHours(1);
            if (isBusinessHour(expectedTime)) {
                hoursAdded++;
            }
        }

        return expectedTime;
    }

}
