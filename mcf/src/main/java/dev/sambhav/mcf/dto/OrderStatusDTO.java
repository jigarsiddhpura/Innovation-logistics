package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusDTO {
    private String status; // Enum as String
    private String message; // Optional: A custom message
}
