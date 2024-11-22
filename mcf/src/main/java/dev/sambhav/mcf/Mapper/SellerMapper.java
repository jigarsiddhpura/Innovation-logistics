package dev.sambhav.mcf.Mapper;

import dev.sambhav.mcf.dto.SellerRequestDTO;
import dev.sambhav.mcf.dto.SellerResponseDTO;
import dev.sambhav.mcf.model.Seller;

public class SellerMapper {
    public static Seller toEntity(SellerRequestDTO dto, Seller seller) {
        seller.setName(dto.getName());
        seller.setEmail(dto.getEmail());
        seller.setPhone(dto.getPhone());
        seller.setShopifyStoreUrl(dto.getShopifyStoreUrl());
        seller.setAmazonMcfAccountId(dto.getAmazonMcfAccountId());
        return seller;
    }

    // Map from Entity to DTO for API responses
    public static SellerResponseDTO toResponseDTO(Seller seller) {
        return new SellerResponseDTO(
                seller.getSellerId(),
                seller.getName(),
                seller.getEmail(),
                seller.getPhone(),
                seller.getShopifyStoreUrl(),
                seller.getAmazonMcfAccountId()
        );
    }
}
