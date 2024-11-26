package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.service.ShopifyProductSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sync")
public class ShopifyProductSyncController {

    @Autowired
    private ShopifyProductSyncService syncService;

    @PostMapping("/shopify/product")
    public ResponseEntity<?> syncShopifyProducts(
            @RequestParam(name = "storeUrl") String storeUrl) {
        try {
            // Clean up the store URL if needed
            storeUrl = storeUrl.trim().toLowerCase();
            List<Product> syncedProducts = syncService.syncShopifyProducts(storeUrl);

            return ResponseEntity.ok(Map.of(
                    "message", "Successfully synced products from Shopify",
                    "count", syncedProducts.size(),
                    "products", syncedProducts
            ));
        } catch (Exception e) {
            log.error("Error in sync endpoint: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to sync products",
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/shopify/products")
    public ResponseEntity<?> deleteAllProducts(@RequestParam String storeUrl) {
        try {
            syncService.deleteAllProducts(storeUrl);
            return ResponseEntity.ok(Map.of(
                    "message", "Successfully deleted all products for store: " + storeUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to delete products",
                    "message", e.getMessage()
            ));
        }
    }
}

