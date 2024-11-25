package dev.sambhav.mcf.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class WebhookVerifier {

    public static boolean verifySignature(String payload, String signature, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmac.init(secretKey);

            byte[] hash = hmac.doFinal(payload.getBytes());
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            return computedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}