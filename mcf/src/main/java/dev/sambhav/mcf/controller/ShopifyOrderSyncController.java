package dev.sambhav.mcf.controller;

import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.service.ShopifyOrderSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;



@RestController
@RequestMapping("/api/sync")
@Slf4j
public class ShopifyOrderSyncController {

    @Autowired
    private ShopifyOrderSyncService syncService;

    @PostMapping("/shopify/orders")
    public ResponseEntity<?> syncShopifyOrders(
            @RequestParam(name = "storeUrl") String storeUrl) {
        try {
            // Clean up the store URL if needed
            storeUrl = storeUrl.trim().toLowerCase();

            List<Order> syncedOrders = syncService.syncShopifyOrders(storeUrl);

            return ResponseEntity.ok(Map.of(
                    "message", "Successfully synced orders from Shopify",
                    "count", syncedOrders.size(),
                    "orders", syncedOrders
            ));
        } catch (Exception e) {
            log.error("Error in sync endpoint: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to sync orders",
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/shopify/orders")
    public ResponseEntity<?> deleteAllOrders(@RequestParam String storeUrl) {
        try {
            syncService.deleteAllOrders(storeUrl);
            return ResponseEntity.ok(Map.of(
                    "message", "Successfully deleted all orders for store: " + storeUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to delete orders",
                    "message", e.getMessage()
            ));
        }
    }
}

