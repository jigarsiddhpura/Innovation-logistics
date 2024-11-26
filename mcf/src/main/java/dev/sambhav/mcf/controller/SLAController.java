package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.dto.OrderPredictionRequest;  // Updated import
import dev.sambhav.mcf.dto.SLAPredictionResult;     // Updated import
import dev.sambhav.mcf.dto.SLARiskLevel;           // Updated import
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.OrderStatus;
import dev.sambhav.mcf.repository.OrderRepository;
import dev.sambhav.mcf.service.SLAService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders/sla")
@Slf4j
public class SLAController {

    private final SLAService slaPredictionService;

    private final OrderRepository orderRepository;

    public SLAController(SLAService slaPredictionService, OrderRepository orderRepository) {
        this.slaPredictionService = slaPredictionService;
        this.orderRepository = orderRepository;
    }


    @PostMapping("/single")
    public ResponseEntity<ApiResponse<SLAPredictionResult>> predictSingleOrderSLA(
            @RequestBody @Valid OrderPredictionRequest request) {
        try {
            log.info("Predicting SLA for order: {}", request.getOrderId());
            SLAPredictionResult prediction = slaPredictionService.predictSLAStatus(request);
            return ResponseEntity.ok(new ApiResponse<>("SLA prediction successful", true, prediction));
        } catch (Exception e) {
            log.error("Error predicting SLA for order: {}", request.getOrderId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error predicting SLA", false, null));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<SLAPredictionResult>>> predictBatchSLA(
            @RequestBody @Valid List<OrderPredictionRequest> requests) {
        try {
            log.info("Processing batch SLA prediction for {} orders", requests.size());
            List<SLAPredictionResult> predictions = requests.stream()
                    .map(slaPredictionService::predictSLAStatus)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Batch SLA prediction successful", true, predictions));
        } catch (Exception e) {
            log.error("Error processing batch SLA prediction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error processing batch prediction", false, null));
        }
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<SLAPredictionResult>> predictOrderSLA(@PathVariable Long orderId) {
        try {
            log.info("Predicting SLA for existing order: {}", orderId);
            OrderPredictionRequest request = buildRequestFromOrder(orderId);
            SLAPredictionResult prediction = slaPredictionService.predictSLAStatus(request);
            return ResponseEntity.ok(new ApiResponse<>("SLA prediction successful", true, prediction));
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Order not found", false, null));
        } catch (Exception e) {
            log.error("Error predicting SLA for order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error predicting SLA", false, null));
        }
    }

    @GetMapping("/risk-summary")
    public ResponseEntity<ApiResponse<Map<SLARiskLevel, Long>>> getRiskLevelSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            log.info("Generating risk level summary for date: {}", targetDate);
            Map<SLARiskLevel, Long> riskSummary = generateRiskSummary(targetDate);
            return ResponseEntity.ok(new ApiResponse<>("Risk level summary generated", true, riskSummary));
        } catch (Exception e) {
            log.error("Error generating risk level summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error generating risk summary", false, null));
        }
    }

    @GetMapping("/at-risk")
    public ResponseEntity<ApiResponse<List<SLAPredictionResult>>> getAtRiskOrders(
            @RequestParam(required = true) String storeUrl,
            @RequestParam(required = false) SLARiskLevel minRiskLevel) {
        try {
            log.info("Fetching at-risk orders for store: {} with minimum risk level: {}", storeUrl, minRiskLevel);

            // Decode URL in case it's encoded
            String decodedStoreUrl = URLDecoder.decode(storeUrl, StandardCharsets.UTF_8.toString());

            List<SLAPredictionResult> atRiskOrders = getOrdersAtRisk(
                    decodedStoreUrl,
                    minRiskLevel != null ? minRiskLevel : SLARiskLevel.MEDIUM
            );

            return ResponseEntity.ok(new ApiResponse<>(
                    String.format("At-risk orders retrieved for store: %s", decodedStoreUrl),
                    true,
                    atRiskOrders
            ));
        } catch (IllegalArgumentException e) {
            log.error("Invalid store URL provided: {}", storeUrl, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Invalid store URL provided", false, null));
        } catch (Exception e) {
            log.error("Error fetching at-risk orders for store: {}", storeUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error fetching at-risk orders", false, null));
        }
    }

    @Data
    @Builder
    public static class ApiResponse<T> {
        private String message;
        private boolean success;
        private T data;
    }

    // Helper methods
    private OrderPredictionRequest buildRequestFromOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Error("Order not found: " + orderId));

        return OrderPredictionRequest.builder()
                .orderId(order.getOrderId())
                .orderCreatedTime(order.getCreatedAt())
                .currentStatus(order.getFulfillmentStatus())
                .lastUpdateTime(order.getProcessedAt())
                .build();
    }

    private Map<SLARiskLevel, Long> generateRiskSummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);

        return orders.stream()
                .map(order -> buildRequestFromOrder(order.getOrderId()))
                .map(slaPredictionService::predictSLAStatus)
                .collect(Collectors.groupingBy(
                        SLAPredictionResult::getRiskLevel,
                        Collectors.counting()
                ));
    }

    private List<SLAPredictionResult> getOrdersAtRisk(String storeUrl, SLARiskLevel minRiskLevel) {
        // Get all orders except pending ones for the specific store
        List<Order> orders = orderRepository.findByStoreUrlAndFulfillmentStatusNot(storeUrl, OrderStatus.SHIPPED);

        // Convert orders to predictions and filter
        return orders.stream()
                .map(order -> slaPredictionService.predictSLAStatus(buildRequestFromOrder(order.getOrderId())))
                .filter(prediction -> prediction.getRiskLevel().compareTo(minRiskLevel) >= 0)
                .collect(Collectors.toList());
    }
}

