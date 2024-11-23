package dev.sambhav.mcf.controller;
import dev.sambhav.mcf.Mapper.OrderMapper;
import dev.sambhav.mcf.dto.OrderRequestDTO;
import dev.sambhav.mcf.dto.OrderResponseDTO;
import dev.sambhav.mcf.dto.WebhookResponseDTO;
import dev.sambhav.mcf.model.Order;
import dev.sambhav.mcf.model.Product;
import dev.sambhav.mcf.service.OrderService;
import dev.sambhav.mcf.service.ProductService;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import dev.sambhav.mcf.service.WebhookService;
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

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    // Webhook for product create
    @PostMapping("/product-created")
    public ResponseEntity<WebhookResponseDTO> handleProductCreated(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        // Verify Shopify Webhook
        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            // Process product creation
            webhookService.processProductCreate(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Product created successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }
    
//    @PostMapping("/order-created")
//    public ResponseEntity<String> handleOrderCreated(
//        @RequestBody String payload,
//        @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {
//
//        // Verify Shopify Webhook (implement this method)
//        if (!verifyShopifyWebhook(payload, hmacHeader)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
//        }
//
//        try {
//            orderService.saveOrderFromWebhook(payload);
//            return ResponseEntity.ok("Order saved successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // Webhook for product update
    @PostMapping("/product-updated")
    public ResponseEntity<WebhookResponseDTO> handleProductUpdated(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        // Verify Shopify Webhook
        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            // Process product update
            webhookService.processProductUpdate(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Product updated successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }

    // Webhook for product delete
    @PostMapping("/product-deleted")
    public ResponseEntity<WebhookResponseDTO> handleProductDeleted(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        // Verify Shopify Webhook
        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            // Process product delete
            Long productId = webhookService.processProductDelete(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Product deleted successfully: ID " + productId, true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }


    // Orders
    // Webhook for order creation
    @PostMapping("/created")
    public ResponseEntity<WebhookResponseDTO> handleOrderCreated(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            webhookService.processOrderCreated(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Order created successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }

    // Webhook for order updates
    @PostMapping("/updated")
    public ResponseEntity<WebhookResponseDTO> handleOrderUpdated(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            webhookService.processOrderUpdated(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Order updated successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }

    // Webhook for order deletion
    @PostMapping("/deleted")
    public ResponseEntity<WebhookResponseDTO> handleOrderDeleted(
            @RequestBody String payload,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmacHeader) {

        if (!webhookService.verifyShopifyWebhook(payload, hmacHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new WebhookResponseDTO("Invalid webhook signature", false));
        }

        try {
            Long deletedOrderId = webhookService.processOrderDeleted(payload);
            return ResponseEntity.ok(new WebhookResponseDTO("Order deleted successfully: ID " + deletedOrderId, true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }

}
