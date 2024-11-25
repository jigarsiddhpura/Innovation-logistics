//package dev.sambhav.mcf.service;
//
//import dev.sambhav.mcf.Mapper.WebhookMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service("dukaanWebhookService")
//public class DukaanWebhookService extends WebhookService {
//
//    @Value("${dukaan.webhook.secret}")
//    private String dukaanSecret;
//
//    public DukaanWebhookService(ProductService productService, WebhookMapper webhookMapper, OrderService orderService) {
//        super(productService, webhookMapper, orderService);
//    }
//
//
//    public boolean verifyWebhook(String payload, String hmacHeader) {
//        return super.verifyWebhook(payload, hmacHeader, dukaanSecret);
//    }
//}
