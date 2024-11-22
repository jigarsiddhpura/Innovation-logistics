package dev.sambhav.mcf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerResponseDTO {
    private Long sellerId;
    private String name;
    private String email;
    private String phone;
    private String shopifyStoreUrl;
    private String amazonMcfAccountId;
}
