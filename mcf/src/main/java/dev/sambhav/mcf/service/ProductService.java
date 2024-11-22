// File: src/main/java/dev/sambhav/mcf/service/ProductService.java
package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.ProductMapper;
import dev.sambhav.mcf.dto.ProductRequest;
import dev.sambhav.mcf.dto.ProductRequestDTO;
import dev.sambhav.mcf.dto.ProductResponseDTO;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.ProductRepository;
import dev.sambhav.mcf.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    // @Autowired
    public ProductService(ProductRepository productRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    public ProductResponseDTO addProduct(ProductRequestDTO productRequestDTO) {
        // Check if seller exists
        Long sellerId = productRequestDTO.getSellerId();
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller with id " + sellerId + " does not exist."));

        // Create and save new Product
        Product product = ProductMapper.toEntity(productRequestDTO);
        product.setSeller(seller);
        return ProductMapper.toResponseDTO(productRepository.save(product));
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
        return ProductMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByName(String name) {
        List<Product> products = productRepository.findByName(name);
        return products.stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
