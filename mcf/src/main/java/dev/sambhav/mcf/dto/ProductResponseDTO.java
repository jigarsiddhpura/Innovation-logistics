package dev.sambhav.mcf.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long productId;
    private String title;
    private String productType;
    private String vendor;
    private String description;
    private BigDecimal price;
    private Integer inventoryLevel;
    private String amazonMcfSku;
    private String storeUrl;
    private StoreType storeType;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
