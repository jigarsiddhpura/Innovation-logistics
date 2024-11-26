package dev.sambhav.mcf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sambhav.mcf.dto.ProductDTO;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.dto.StoreType;
import dev.sambhav.mcf.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShopifyProductSyncService {

    @Value("${shopify.access.token}")
    private String shopifyAccessToken;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Product> syncShopifyProducts(String storeUrl) {
        try {
            // Remove any https:// or http:// if present
            storeUrl = storeUrl.replaceAll("^(https?://)", "");

            // Construct the correct API URL
            String shopifyApiUrl = String.format("https://%s/admin/api/2024-01/products.json", storeUrl);
            log.info("Calling Shopify API: {}", shopifyApiUrl);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Shopify-Access-Token", shopifyAccessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make request
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Making request to Shopify with token: {}", shopifyAccessToken.substring(0, 5) + "...");

            ResponseEntity<String> response = restTemplate.exchange(
                    shopifyApiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode products = root.get("products");

                List<Product> savedProducts = new ArrayList<>();

                if (products != null && products.isArray()) {
                    for (JsonNode productNode : products) {
                        try {
                            Product product = mapShopifyProductToEntity(productNode, storeUrl);
                            savedProducts.add(productRepository.save(product));
                            log.info("Saved product: {}", product.getTitle());
                        } catch (Exception e) {
                            log.error("Error processing product: {}", productNode.get("title"), e);
                        }
                    }

                    log.info("Successfully synced {} products from Shopify", savedProducts.size());
                } else {
                    log.warn("No products found in the response");
                }

                return savedProducts;
            } else {
                log.error("Failed to fetch products from Shopify. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to fetch products from Shopify: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error syncing products from Shopify: {}", e.getMessage(), e);
            throw new RuntimeException("Error syncing products from Shopify: " + e.getMessage(), e);
        }
    }

    private Product mapShopifyProductToEntity(JsonNode productNode, String storeUrl) {
        Product product = new Product();

        // Get the first variant (assuming it's the main one)
        JsonNode firstVariant = productNode.get("variants").get(0);

        product.setProductId(Long.parseLong(firstVariant.get("product_id").asText()));
        product.setTitle(productNode.get("title").asText());
        product.setProductType(productNode.get("product_type").asText());
        product.setVendor(productNode.get("vendor").asText());
        product.setDescription(productNode.get("body_html").asText());

        // Price handling
        String priceStr = firstVariant.get("price").asText();
        product.setPrice(new BigDecimal(priceStr));

        // Inventory handling
        product.setInventoryLevel(firstVariant.get("inventory_quantity").asInt());

        // SKU handling
//        if (firstVariant.has("sku") && !firstVariant.get("sku").isNull()) {
//            product.setAmazonMcfSku(firstVariant.get("sku").asText());
//        }

        // Store information
        product.setStoreUrl(storeUrl);
        product.setStoreType(StoreType.SHOPIFY);

        // Timestamps
        if (productNode.has("published_at") && !productNode.get("published_at").isNull()) {
            product.setPublishedAt(
                    OffsetDateTime.parse(productNode.get("published_at").asText())
                            .toLocalDateTime()
            );
        } else {
            product.setPublishedAt(LocalDateTime.now());
        }

        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }

    public void deleteAllProducts(String storeUrl) {
        productRepository.deleteByStoreUrl(storeUrl);
    }
}
