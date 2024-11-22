package dev.sambhav.mcf.dto;

import lombok.Data;

@Data
public class SellerRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String password; // Plain-text password for hashing
    private String shopifyStoreUrl;
    private String amazonMcfAccountId;
}
