//package dev.sambhav.mcf.service;
//
//import dev.sambhav.mcf.Mapper.WebhookMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service("shopifyWebhookService")
//public class ShopifyWebhookService extends WebhookService {
//
//    @Value("${shopify.webhook.secret}")
//    private String shopifySecret;
//
//    public ShopifyWebhookService(ProductService productService, WebhookMapper webhookMapper, OrderService orderService) {
//        super(productService, webhookMapper, orderService);
//    }
//
//    public boolean verifyWebhook(String payload, String hmacHeader) {
//        return super.verifyWebhook(payload, hmacHeader, shopifySecret);
//    }
//}
