package dev.sambhav.mcf.controller;
import dev.sambhav.mcf.Mapper.OrderMapper;
import dev.sambhav.mcf.dto.OrderRequestDTO;
import dev.sambhav.mcf.dto.OrderResponseDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.service.OrderService;
import dev.sambhav.mcf.service.ProductService;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/product-listed")
    public ResponseEntity<String> handleProductListed(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        // Verify Shopify Webhook (implement this method)
        if (!verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
        }

        // Process and save product data
        try {
            productService.saveProductFromWebhook(payload);
            return ResponseEntity.ok("Product saved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
    
        @PostMapping("/order-created")
        public ResponseEntity<String> handleOrderCreated(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {
    
            // Verify Shopify Webhook (implement this method)
            if (!verifyShopifyWebhook(payload, hmacHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }
    
            try {
                orderService.saveOrderFromWebhook(payload);
                return ResponseEntity.ok("Order saved successfully");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    private boolean verifyShopifyWebhook(String payload, String hmacHeader) {
        try {
            String secret = "0250d998304ef6ae8df7d07f6a17475f5114f90338e687439048fd801648dc78"; // Replace with your actual webhook secret
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(payload.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hash);
            return hmacHeader.equals(calculatedHmac);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
