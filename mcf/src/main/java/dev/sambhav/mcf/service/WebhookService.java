package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.WebhookMapper;
import dev.sambhav.mcf.dto.OrderDto;
import dev.sambhav.mcf.dto.ProductDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.security.MessageDigest;

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
