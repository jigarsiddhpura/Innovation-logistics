package dev.sambhav.mcf.Mapper;

import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.dto.ProductRequestDTO;
import dev.sambhav.mcf.dto.ProductResponseDTO;
import dev.sambhav.mcf.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .productType(product.getProductType())
                .vendor(product.getVendor())
                .description(product.getDescription())
                .price(product.getPrice())
                .inventoryLevel(product.getInventoryLevel())
                .storeUrl(product.getStoreUrl())
                .storeType(product.getStoreType())
                .publishedAt(product.getPublishedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setTitle(dto.getTitle());
        product.setProductType(dto.getProductType());
        product.setVendor(dto.getVendor());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setInventoryLevel(dto.getInventoryLevel());
        product.setAmazonMcfSku(dto.getAmazonMcfSku());
        product.setStoreUrl(dto.getStoreUrl());
        product.setStoreType(dto.getStoreType());
        return product;
    }

    public static ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDTO(
                product.getProductId(),
                product.getTitle(),
                product.getProductType(),
                product.getVendor(),
                product.getDescription(),
                product.getPrice(),
                product.getInventoryLevel(),
                product.getAmazonMcfSku(),
                product.getStoreUrl(),
                product.getStoreType(),
                product.getPublishedAt(),
                product.getUpdatedAt()
        );
    }

    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setTitle(dto.getTitle());
        product.setProductType(dto.getProductType());
        product.setVendor(dto.getVendor());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setInventoryLevel(dto.getInventoryLevel());
        product.setAmazonMcfSku(dto.getAmazonMcfSku());
        product.setStoreUrl(dto.getStoreUrl());
        product.setStoreType(dto.getStoreType());
        product.setPublishedAt(dto.getPublishedAt());
        product.setUpdatedAt(dto.getUpdatedAt());

        return product;
    }
}