package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.StockSyncService;
import com.ornek.stoktakip.integration.PlatformIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    
    private final PlatformService platformService;
    private final StockSyncService stockSyncService;
    private final Map<String, PlatformIntegrationService> integrationServices;
    
    @Autowired
    public WebhookController(PlatformService platformService, 
                           StockSyncService stockSyncService,
                           Map<String, PlatformIntegrationService> integrationServices) {
        this.platformService = platformService;
        this.stockSyncService = stockSyncService;
        this.integrationServices = integrationServices;
    }
    
    /**
     * eBay webhook endpoint'i
     */
    @PostMapping("/ebay")
    public ResponseEntity<String> handleEbayWebhook(@RequestBody Map<String, Object> webhookData,
                                                   @RequestHeader Map<String, String> headers) {
        try {
            Platform platform = getPlatformByCode("EBAY");
            if (platform == null) {
                return ResponseEntity.badRequest().body("eBay platform bulunamadı");
            }
            
            // Webhook doğrulama
            if (!verifyEbayWebhook(webhookData, headers)) {
                return ResponseEntity.badRequest().body("Geçersiz webhook imzası");
            }
            
            // Webhook'u işle
            PlatformIntegrationService integrationService = integrationServices.get("ebayIntegrationService");
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, webhookData);
                if (success) {
                    return ResponseEntity.ok("Webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook hatası: " + e.getMessage());
        }
    }
    
    /**
     * Shopify webhook endpoint'i
     */
    @PostMapping("/shopify")
    public ResponseEntity<String> handleShopifyWebhook(@RequestBody Map<String, Object> webhookData,
                                                      @RequestHeader Map<String, String> headers) {
        try {
            Platform platform = getPlatformByCode("SHOPIFY");
            if (platform == null) {
                return ResponseEntity.badRequest().body("Shopify platform bulunamadı");
            }
            
            // Webhook doğrulama
            if (!verifyShopifyWebhook(webhookData, headers)) {
                return ResponseEntity.badRequest().body("Geçersiz webhook imzası");
            }
            
            // Webhook'u işle
            PlatformIntegrationService integrationService = integrationServices.get("shopifyIntegrationService");
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, webhookData);
                if (success) {
                    return ResponseEntity.ok("Webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook hatası: " + e.getMessage());
        }
    }
    
    /**
     * Amazon webhook endpoint'i
     */
    @PostMapping("/amazon")
    public ResponseEntity<String> handleAmazonWebhook(@RequestBody Map<String, Object> webhookData,
                                                     @RequestHeader Map<String, String> headers) {
        try {
            Platform platform = getPlatformByCode("AMAZON");
            if (platform == null) {
                return ResponseEntity.badRequest().body("Amazon platform bulunamadı");
            }
            
            // Webhook doğrulama
            if (!verifyAmazonWebhook(webhookData, headers)) {
                return ResponseEntity.badRequest().body("Geçersiz webhook imzası");
            }
            
            // Webhook'u işle
            PlatformIntegrationService integrationService = integrationServices.get("amazonIntegrationService");
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, webhookData);
                if (success) {
                    return ResponseEntity.ok("Webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook hatası: " + e.getMessage());
        }
    }
    
    /**
     * Trendyol webhook endpoint'i
     */
    @PostMapping("/trendyol")
    public ResponseEntity<String> handleTrendyolWebhook(@RequestBody Map<String, Object> webhookData,
                                                       @RequestHeader Map<String, String> headers) {
        try {
            Platform platform = getPlatformByCode("TRENDYOL");
            if (platform == null) {
                return ResponseEntity.badRequest().body("Trendyol platform bulunamadı");
            }
            
            // Webhook doğrulama
            if (!verifyTrendyolWebhook(webhookData, headers)) {
                return ResponseEntity.badRequest().body("Geçersiz webhook imzası");
            }
            
            // Webhook'u işle
            PlatformIntegrationService integrationService = integrationServices.get("trendyolIntegrationService");
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, webhookData);
                if (success) {
                    return ResponseEntity.ok("Webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook hatası: " + e.getMessage());
        }
    }
    
    /**
     * Genel webhook endpoint'i (platform kodu ile)
     */
    @PostMapping("/{platformCode}")
    public ResponseEntity<String> handleGenericWebhook(@PathVariable String platformCode,
                                                      @RequestBody Map<String, Object> webhookData,
                                                      @RequestHeader Map<String, String> headers) {
        try {
            Platform platform = getPlatformByCode(platformCode.toUpperCase());
            if (platform == null) {
                return ResponseEntity.badRequest().body("Platform bulunamadı: " + platformCode);
            }
            
            // Webhook doğrulama
            if (!verifyGenericWebhook(platform, webhookData, headers)) {
                return ResponseEntity.badRequest().body("Geçersiz webhook imzası");
            }
            
            // Webhook'u işle
            String serviceName = platformCode.toLowerCase() + "IntegrationService";
            PlatformIntegrationService integrationService = integrationServices.get(serviceName);
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, webhookData);
                if (success) {
                    return ResponseEntity.ok("Webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Webhook hatası: " + e.getMessage());
        }
    }
    
    /**
     * Webhook test endpoint'i
     */
    @PostMapping("/test/{platformCode}")
    public ResponseEntity<String> testWebhook(@PathVariable String platformCode,
                                            @RequestBody Map<String, Object> testData) {
        try {
            Platform platform = getPlatformByCode(platformCode.toUpperCase());
            if (platform == null) {
                return ResponseEntity.badRequest().body("Platform bulunamadı: " + platformCode);
            }
            
            String serviceName = platformCode.toLowerCase() + "IntegrationService";
            PlatformIntegrationService integrationService = integrationServices.get(serviceName);
            if (integrationService != null) {
                boolean success = integrationService.processWebhook(platform, testData);
                if (success) {
                    return ResponseEntity.ok("Test webhook başarıyla işlendi");
                }
            }
            
            return ResponseEntity.internalServerError().body("Test webhook işlenemedi");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Test webhook hatası: " + e.getMessage());
        }
    }
    
    private Platform getPlatformByCode(String code) {
        return platformService.getPlatformByCode(code).orElse(null);
    }
    
    private boolean verifyEbayWebhook(Map<String, Object> webhookData, Map<String, String> headers) {
        // eBay webhook doğrulama mantığı
        String signature = headers.get("x-ebay-signature");
        return signature != null && !signature.isEmpty();
    }
    
    private boolean verifyShopifyWebhook(Map<String, Object> webhookData, Map<String, String> headers) {
        // Shopify webhook doğrulama mantığı
        String signature = headers.get("x-shopify-hmac-sha256");
        return signature != null && !signature.isEmpty();
    }
    
    private boolean verifyAmazonWebhook(Map<String, Object> webhookData, Map<String, String> headers) {
        // Amazon webhook doğrulama mantığı
        String signature = headers.get("x-amz-signature");
        return signature != null && !signature.isEmpty();
    }
    
    private boolean verifyTrendyolWebhook(Map<String, Object> webhookData, Map<String, String> headers) {
        // Trendyol webhook doğrulama mantığı
        String signature = headers.get("x-trendyol-signature");
        return signature != null && !signature.isEmpty();
    }
    
    private boolean verifyGenericWebhook(Platform platform, Map<String, Object> webhookData, Map<String, String> headers) {
        // Genel webhook doğrulama mantığı
        // Platform'a özel doğrulama yapılabilir
        return true; // Şimdilik basit doğrulama
    }
}
