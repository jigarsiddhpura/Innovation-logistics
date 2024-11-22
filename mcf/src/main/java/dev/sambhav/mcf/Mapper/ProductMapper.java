package dev.sambhav.mcf.Mapper;

import dev.sambhav.mcf.dto.ProductRequestDTO;
import dev.sambhav.mcf.dto.ProductResponseDTO;
import dev.sambhav.mcf.model.Product;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getInventoryLevel(),
                product.getReorderThreshold()
        );
    }

    public static Product toEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setInventoryLevel(productRequestDTO.getInventoryLevel());
        product.setReorderThreshold(productRequestDTO.getReorderThreshold());
        return product;
    }
}
