package dev.sambhav.mcf.Mapper;

import dev.sambhav.mcf.dto.ProductRequestDTO;
import dev.sambhav.mcf.dto.ProductResponseDTO;
import dev.sambhav.mcf.model.Product;

public class ProductMapper {

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getInventoryLevel()
        );
    }

    public static Product toEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setTitle(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setInventoryLevel(productRequestDTO.getInventoryLevel());
        return product;
    }
}
