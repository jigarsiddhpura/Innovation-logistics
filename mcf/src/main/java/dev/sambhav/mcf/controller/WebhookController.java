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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    private final WebhookService webhookService;
    private final WebhookService shopifyWebhookService;
    private final WebhookService dukaanWebhookService;

    @Autowired
    public WebhookController(WebhookService webhookService, WebhookService shopifyWebhookService, WebhookService dukaanWebhookService) {
        this.webhookService = webhookService;
        this.shopifyWebhookService = shopifyWebhookService;
        this.dukaanWebhookService = dukaanWebhookService;
    }
    @PostMapping("/product-create")
    public ResponseEntity<WebhookResponseDTO> handleProductCreated(
            @RequestBody String payload,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

        try {
            String platform;
            boolean isVerified = true;  // Default to true

            // Simple platform check based on Shopify header
            if (shopifyHmacHeader == null) {
                platform = "dukaan";  // If no Shopify header, it's Dukaan
            } else {
                platform = "shopify";
            }
//
            // Process payload based on platform
            if ("shopify".equals(platform)) {
                webhookService.processProductCreateShopify(payload, platform);
            } else {
                webhookService.processProductCreateDukaan(payload, platform);
            }

            return ResponseEntity.ok(new WebhookResponseDTO("Product created successfully", true));

        } catch (Exception e) {
            log.error("Error processing webhook: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook: " + e.getMessage(), false));
        }
    }



    @PostMapping("/order-create")
    public ResponseEntity<WebhookResponseDTO> handleOrderCreated(
            @RequestBody String payload,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

        try {
            String platform;
            boolean isVerified = true;

            // Identify platform
            if (shopifyHmacHeader == null) {
                platform = "dukaan";
            } else {
                platform = "shopify";
            }

            // Process payload based on platform
            if ("shopify".equals(platform)) {
                webhookService.processOrderCreateShopify(payload, platform);
            } else {
                webhookService.processOrderCreateDukaan(payload, platform);
            }

            return ResponseEntity.ok(new WebhookResponseDTO("Order created successfully", true));
        } catch (Exception e) {
            log.error("Error processing order webhook: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook: " + e.getMessage(), false));
        }
    }

//     Webhook for product update
@PostMapping("/product-updated")
public ResponseEntity<WebhookResponseDTO> handleProductUpdated(
        @RequestBody String payload,
        @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

    try {
        String platform;

        // Determine the platform based on the Shopify header
        if (shopifyHmacHeader != null) {
            platform = "shopify";

            // Verify Shopify Webhook signature
            if (!webhookService.verifyShopifySignature(payload, shopifyHmacHeader)) {
                System.out.println("Invalid Shopify webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new WebhookResponseDTO("Invalid Shopify webhook signature", false));
            }
        } else {
            platform = "dukaan";
        }

        // Process product update based on the platform
        if ("shopify".equals(platform)) {
            webhookService.processProductUpdateShopify(payload, platform);
        } else if ("dukaan".equals(platform)) {
            webhookService.processProductUpdateDukaan(payload, platform);
        }

        return ResponseEntity.ok(new WebhookResponseDTO("Product updated successfully", true));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WebhookResponseDTO("Error processing webhook", false));
    }
}
//
//    // Webhook for product delete
@PostMapping("/product-deleted")
public ResponseEntity<WebhookResponseDTO> handleProductDeleted(
        @RequestBody String payload,
        @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

    try {
        String platform;

        // Determine the platform based on the Shopify header
        if (shopifyHmacHeader != null) {
            platform = "shopify";

            // Verify Shopify Webhook signature
            if (!webhookService.verifyShopifySignature(payload, shopifyHmacHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new WebhookResponseDTO("Invalid Shopify webhook signature", false));
            }
        } else {
            platform = "dukaan";
        }

        // Process product deletion based on the platform
        Long productId;
        if ("shopify".equals(platform)) {
            productId = webhookService.processProductDeleteShopify(payload, platform);
        } else if ("dukaan".equals(platform)) {
            productId = webhookService.processProductDeleteDukaan(payload, platform);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        return ResponseEntity.ok(new WebhookResponseDTO("Product deleted successfully: ID " + productId, true));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WebhookResponseDTO("Error processing webhook", false));
    }
}

    // Webhook for order updates
@PostMapping("/order-updated")
public ResponseEntity<WebhookResponseDTO> handleOrderUpdated(
        @RequestBody String payload,
        @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

    try {
        String platform;

        // Determine the platform based on the Shopify header
        if (shopifyHmacHeader != null) {
            platform = "shopify";

            // Verify Shopify Webhook signature
            if (!webhookService.verifyShopifySignature(payload, shopifyHmacHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new WebhookResponseDTO("Invalid Shopify webhook signature", false));
            }
        } else {
            platform = "dukaan";
        }

        // Process order update based on the platform
        webhookService.processOrderUpdated(payload, platform);

        return ResponseEntity.ok(new WebhookResponseDTO("Order updated successfully", true));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WebhookResponseDTO("Error processing webhook", false));
    }
}
//
//    // Webhook for order deletion
@PostMapping("/order-deleted")
public ResponseEntity<WebhookResponseDTO> handleOrderDeleted(
        @RequestBody String payload,
        @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

    try {
        String platform;

        // Determine the platform based on the Shopify header
        if (shopifyHmacHeader != null) {
            platform = "shopify";

            // Verify Shopify Webhook signature
            if (!webhookService.verifyShopifySignature(payload, shopifyHmacHeader)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new WebhookResponseDTO("Invalid Shopify webhook signature", false));
            }
        } else {
            platform = "dukaan";
        }

        // Process order deletion based on the platform
        Long deletedOrderId = webhookService.processOrderDeleted(payload, platform);

        return ResponseEntity.ok(new WebhookResponseDTO("Order deleted successfully: ID " + deletedOrderId, true));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new WebhookResponseDTO("Error processing webhook", false));
    }
}

    @PostMapping("/order-status-update")
    public ResponseEntity<WebhookResponseDTO> receiveStatusUpdate(
            @RequestBody String payload,
            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String shopifyHmacHeader) {

        try {
            String platform;

            // Determine the platform based on the Shopify header
            if (shopifyHmacHeader != null) {
                platform = "shopify";

                // Verify Shopify Webhook signature
                if (!webhookService.verifyShopifySignature(payload, shopifyHmacHeader)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new WebhookResponseDTO("Invalid Shopify webhook signature", false));
                }
            } else {
                platform = "dukaan";
            }

//            // Process order update based on the platform
//            webhookService.processOrderUpdated(payload, platform);
            log.info(payload);

            return ResponseEntity.ok(new WebhookResponseDTO("Order updated successfully", true));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new WebhookResponseDTO("Error processing webhook", false));
        }
    }

}
