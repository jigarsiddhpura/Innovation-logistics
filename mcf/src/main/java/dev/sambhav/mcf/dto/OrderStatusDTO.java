package dev.sambhav.mcf.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusDTO {
    private String fulfillmentStatus; // Enum as String
    private LocalDateTime deliveryEta;
    private Boolean slaMet;
    private String message; // Optional: A custom message
}
