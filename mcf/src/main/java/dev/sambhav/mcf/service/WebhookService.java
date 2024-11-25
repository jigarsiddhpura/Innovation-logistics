package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.WebhookMapper;
import dev.sambhav.mcf.dto.OrderDTO;
import dev.sambhav.mcf.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class WebhookService {

    private final ProductService productService;
    private final WebhookMapper webhookMapper;
    private final OrderService orderService;


    @Value("${shopify.webhook.secret}")
    private String shopifySecret;

    @Value("${dukaan.webhook.secret}")
    private String dukaanSecret;

    public WebhookService(ProductService productService, WebhookMapper webhookMapper, OrderService orderService) {
        this.productService = productService;
        this.webhookMapper = webhookMapper;
        this.orderService = orderService;
    }

//    public boolean verifyWebhookSignature(String payload, String platform, String shopifyHmacHeader, String dukaanSignatureHeader) {
//        try {
//            String secret = "shopify".equals(platform) ? shopifySecret : dukaanSecret;
//            String header = "shopify".equals(platform) ? shopifyHmacHeader : dukaanSignatureHeader;
//
//            if (secret == null || header == null) {
//                return false;
//            }
//
//            Mac hmac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//            hmac.init(secretKey);
//            byte[] hash = hmac.doFinal(payload.getBytes());
//            String calculatedSignature = Base64.getEncoder().encodeToString(hash);
//
//            return header.equals(calculatedSignature);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean verifyShopifySignature(String payload, String shopifyHmacHeader) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(shopifySecret.getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hash = hmac.doFinal(payload.getBytes());
            String calculatedSignature = Base64.getEncoder().encodeToString(hash);

            return shopifyHmacHeader.equals(calculatedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    public void processProductCreateShopify(String payload, String platform) {
        ProductDTO productDto = webhookMapper.mapToProductDto(payload,platform);
        productService.saveProduct(productDto);
    }

    public void processProductCreateDukaan(String payload, String platform) {
        ProductDTO productDto = webhookMapper.mapToProductDto(payload,platform);
        productService.saveProduct(productDto);
    }

    public void processOrderCreateShopify(String payload, String platform) {
        OrderDTO orderDto = webhookMapper.mapToOrderDto(payload, platform);
        orderService.saveOrder(orderDto);
    }

    public void processOrderCreateDukaan(String payload, String platform) {
        OrderDTO orderDto = webhookMapper.mapToOrderDto(payload, platform);
        orderService.saveOrder(orderDto);
    }

    public void processProductUpdateShopify(String payload, String platform) {
        // Logic to process Shopify product update
        ProductDTO productDto = webhookMapper.mapToProductDto(payload, platform);
        productService.updateProduct(productDto);
    }

    public void processProductUpdateDukaan(String payload, String platform) {
        // Logic to process Dukaan product update
        ProductDTO productDto = webhookMapper.mapToProductDto(payload, platform);
        productService.updateProduct(productDto);
    }

    public Long processProductDeleteShopify(String payload, String platform) {
        // Logic to process Shopify product deletion
        Long productId = webhookMapper.mapToProductId(payload);
        productService.deleteProductById(productId);
        return productId;
    }

    public Long processProductDeleteDukaan(String payload, String platform) {
        // Logic to process Dukaan product deletion
        Long productId = webhookMapper.mapToProductId(payload);
        productService.deleteProductById(productId);
        return productId;
    }

    public void processOrderUpdated(String payload, String platform) {
        if ("shopify".equals(platform)) {
            processOrderUpdatedShopify(payload);
        } else if ("dukaan".equals(platform)) {
            processOrderUpdatedDukaan(payload);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    public void processOrderUpdatedShopify(String payload) {
        // Logic to process Shopify order update
        OrderDTO orderDto = webhookMapper.mapToOrderDto(payload, "shopify");
        orderService.updateOrder(orderDto);
    }

    public void processOrderUpdatedDukaan(String payload) {
        // Logic to process Dukaan order update
        OrderDTO orderDto = webhookMapper.mapToOrderDto(payload, "dukaan");
        orderService.updateOrder(orderDto);
    }

    public Long processOrderDeleted(String payload, String platform) {
        if ("shopify".equals(platform)) {
            return processOrderDeletedShopify(payload);
        } else if ("dukaan".equals(platform)) {
            return processOrderDeletedDukaan(payload);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    public Long processOrderDeletedShopify(String payload) {
        // Logic to process Shopify order deletion
        Long orderId = webhookMapper.mapToOrderId(payload);
        orderService.deleteOrder(orderId);
        return orderId;
    }

    public Long processOrderDeletedDukaan(String payload) {
        // Logic to process Dukaan order deletion
        Long orderId = webhookMapper.mapToOrderId(payload);
        orderService.deleteOrder(orderId);
        return orderId;
    }

//    @Transactional
//    public void processProductCreate(String payload) {
//        ProductDTO productDto = webhookMapper.mapToProductDto(payload);
//        productService.saveProduct(productDto);
//    }

//    @Transactional
//    public void processProductUpdate(String payload) {
//        ProductDTO productDto = webhookMapper.mapToProductDto(payload);
//        productService.updateProduct(productDto);
//    }
//
//    @Transactional
//    public Long processProductDelete(String payload) {
//        Long productId = webhookMapper.mapToProductId(payload);
//        productService.deleteProductById(productId);
//        return productId;
//    }
//
//    //ORDERS
//
//    @Transactional
//    public void processOrderCreated(String payload) {
//        OrderDto orderDto = webhookMapper.mapToOrderDto(payload);
//        orderService.saveOrder(orderDto);
//    }
//
//    @Transactional
//    public void processOrderUpdated(String payload) {
//        OrderDto orderDto = webhookMapper.mapToOrderDto(payload);
//        orderService.updateOrder(orderDto);
//    }
//
//    @Transactional
//    public Long processOrderDeleted(String payload) {
//        Long orderId = webhookMapper.mapToOrderId(payload);
//        orderService.deleteOrder(orderId);
//        return orderId;
//    }
}
