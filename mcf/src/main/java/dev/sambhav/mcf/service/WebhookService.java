package dev.sambhav.mcf.service;

import dev.sambhav.mcf.Mapper.WebhookMapper;
import dev.sambhav.mcf.dto.OrderDto;
import dev.sambhav.mcf.dto.ProductDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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


//    @Value("${shopify.webhook.secret}")
//    private String shopifySecret;

    public WebhookService(ProductService productService, WebhookMapper webhookMapper, OrderService orderService) {
        this.productService = productService;
        this.webhookMapper = webhookMapper;
        this.orderService = orderService;
    }


    public boolean verifyShopifyWebhook(String payload, String hmacHeader) {
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

    @Transactional
    public void processProductCreate(String payload) {
        ProductDTO productDto = webhookMapper.mapToProductDto(payload);
        productService.saveProduct(productDto);
    }

    @Transactional
    public void processProductUpdate(String payload) {
        ProductDTO productDto = webhookMapper.mapToProductDto(payload);
        productService.updateProduct(productDto);
    }

    @Transactional
    public Long processProductDelete(String payload) {
        Long productId = webhookMapper.mapToProductId(payload);
        productService.deleteProductById(productId);
        return productId;
    }

    //ORDERS

    @Transactional
    public void processOrderCreated(String payload) {
        OrderDto orderDto = webhookMapper.mapToOrderDto(payload);
        orderService.saveOrder(orderDto);
    }

    @Transactional
    public void processOrderUpdated(String payload) {
        OrderDto orderDto = webhookMapper.mapToOrderDto(payload);
        orderService.updateOrder(orderDto);
    }

    @Transactional
    public Long processOrderDeleted(String payload) {
        Long orderId = webhookMapper.mapToOrderId(payload);
        orderService.deleteOrder(orderId);
        return orderId;
    }
}
