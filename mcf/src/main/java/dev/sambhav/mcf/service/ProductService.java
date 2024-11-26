// File: src/main/java/dev/sambhav/mcf/service/ProductService.java
package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.ProductMapper;
import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.dto.ProductRequestDTO;
import dev.sambhav.mcf.dto.ProductResponseDTO;
import dev.sambhav.mcf.dto.StoreType;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.model.Seller;
import dev.sambhav.mcf.repository.ProductRepository;
import dev.sambhav.mcf.repository.SellerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        Long sellerId = productRequestDTO.getSellerId();
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Seller with id " + sellerId + " does not exist."));

        Product product = ProductMapper.toEntity(productRequestDTO);
        validateAndSetStoreType(product);
        return ProductMapper.toResponseDTO(productRepository.save(product));
    }

    private void validateAndSetStoreType(Product product) {
        if (product.getStoreUrl() == null || product.getStoreUrl().isEmpty()) {
            throw new IllegalArgumentException("Store URL cannot be empty");
        }

        String storeUrl = product.getStoreUrl().toLowerCase();
        if (storeUrl.contains("shopify.com")) {
            product.setStoreType(StoreType.SHOPIFY);
        } else if (storeUrl.contains("dukaan.com")) {
            product.setStoreType(StoreType.DUKAAN);
        } else {
            throw new IllegalArgumentException("Invalid store URL. Must be from Shopify or Dukaan");
        }
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
        return ProductMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByName(String name) {
        List<Product> products = productRepository.findByVendor(name);
        return products.stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAllProducts(String storeUrl) {
        List<Product> products;
        if (storeUrl != null && !storeUrl.isEmpty()) {
            products = productRepository.findByStoreUrl(storeUrl);
        } else {
            products = productRepository.findAll();
        }
        return products.stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateProduct(ProductDTO productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productDto.getProductId()));

        product.setTitle(productDto.getTitle());
        product.setProductType(productDto.getProductType());
        product.setVendor(productDto.getVendor());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setInventoryLevel(productDto.getInventoryLevel());

        if (productDto.getStoreUrl() != null) {
            product.setStoreUrl(productDto.getStoreUrl());
            validateAndSetStoreType(product);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public void saveProduct(ProductDTO productDto) {
        log.info(String.valueOf(productDto));
        Product product = mapToEntity(productDto);
        productRepository.save(product);
    }

    private Product mapToEntity(ProductDTO productDto) {
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setTitle(productDto.getTitle());
        product.setProductType(productDto.getProductType());
        product.setVendor(productDto.getVendor());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setInventoryLevel(productDto.getInventoryLevel());
        product.setPublishedAt(productDto.getPublishedAt());
        product.setUpdatedAt(productDto.getUpdatedAt());
        product.setStoreType(productDto.getStoreType());
        product.setStoreUrl(productDto.getStoreUrl());
        product.setAmazonMcfSku(productDto.getAmazonMcfSku());
        return product;
    }

//    @Transactional
//    public void saveProductFromWebhook(String payload) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode rootNode = objectMapper.readTree(payload);
//
//        // Extract product details
//        Product product = new Product();
//        product.setProductId(rootNode.get("variants").get(0).get("product_id").asLong());
//        product.setTitle(rootNode.get("title").asText());
//        product.setProductType(rootNode.get("product_type").asText());
//        product.setVendor(rootNode.get("vendor").asText());
//        product.setDescription(rootNode.get("body_html").asText());
//        product.setPrice(new BigDecimal(rootNode.get("variants").get(0).get("price").asText())); // First variant price
//        product.setInventoryLevel(rootNode.get("variants").get(0).get("inventory_quantity").asInt());
//        product.setAmazonMcfSku(rootNode.get("variants").get(0).get("sku").asText());
//        product.setPublishedAt(OffsetDateTime.parse(rootNode.get("published_at").asText().replace("Z", "")).toLocalDateTime());
//        product.setUpdatedAt(OffsetDateTime.parse(rootNode.get("updated_at").asText().replace("Z", "")).toLocalDateTime());
//
//        // Save to database
//        productRepository.save(product);
//    }
}
