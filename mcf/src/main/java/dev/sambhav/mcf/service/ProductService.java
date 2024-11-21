// File: src/main/java/dev/sambhav/mcf/service/ProductService.java
package dev.sambhav.mcf.service;

import dev.sambhav.mcf.dto.ProductRequest;
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

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    // @Autowired
    public ProductService(ProductRepository productRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    public Product addProduct(ProductRequest productRequest) {
        // Check if seller exists
        Long sellerId = productRequest.getSellerId();
        Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);
        if (!sellerOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller with id " + sellerId + " does not exist.");
        }
        Seller seller = sellerOptional.get();

        // Create new Product
        Product product = new Product();
        product.setSeller(seller);
        product.setShopifyProductId(productRequest.getShopifyProductId());
        product.setAmazonMcfSku(productRequest.getAmazonMcfSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setInventoryLevel(productRequest.getInventoryLevel());
        product.setReorderThreshold(productRequest.getReorderThreshold());

        // Save product
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
