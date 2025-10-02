package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.integration.PlatformIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Platform bağlantı testi servisi
 */
@Service
public class PlatformTestService {
    
    private final Map<String, PlatformIntegrationService> integrationServices;
    
    @Autowired
    public PlatformTestService(Map<String, PlatformIntegrationService> integrationServices) {
        this.integrationServices = integrationServices;
    }
    
    /**
     * Platform bağlantısını test eder
     */
    public boolean testPlatformConnection(Platform platform) {
        try {
            String serviceName = platform.getCode().toLowerCase() + "IntegrationService";
            PlatformIntegrationService integrationService = integrationServices.get(serviceName);
            
            if (integrationService == null) {
                throw new RuntimeException("Platform entegrasyon servisi bulunamadı: " + serviceName);
            }
            
            return integrationService.testConnection(platform);
            
        } catch (Exception e) {
            throw new RuntimeException("Platform bağlantı testi hatası: " + e.getMessage(), e);
        }
    }
    
    /**
     * Platform API kimlik bilgilerini doğrular
     */
    public boolean validateCredentials(Platform platform) {
        try {
            // Temel validasyonlar
            if (platform.getApiKey() == null || platform.getApiKey().trim().isEmpty()) {
                throw new RuntimeException("API Key boş olamaz");
            }
            
            if (platform.getApiSecret() == null || platform.getApiSecret().trim().isEmpty()) {
                throw new RuntimeException("API Secret boş olamaz");
            }
            
            if (platform.getApiEndpoint() == null || platform.getApiEndpoint().trim().isEmpty()) {
                throw new RuntimeException("API Endpoint boş olamaz");
            }
            
            // Platform'a özel validasyonlar
            switch (platform.getCode().toUpperCase()) {
                case "EBAY":
                    return validateEbayCredentials(platform);
                case "SHOPIFY":
                    return validateShopifyCredentials(platform);
                case "AMAZON":
                    return validateAmazonCredentials(platform);
                case "TRENDYOL":
                    return validateTrendyolCredentials(platform);
                default:
                    return true; // Bilinmeyen platformlar için temel validasyon yeterli
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Kimlik bilgisi validasyon hatası: " + e.getMessage(), e);
        }
    }
    
    /**
     * Platform'a özel test verisi gönderir
     */
    public Map<String, Object> sendTestData(Platform platform) {
        try {
            String serviceName = platform.getCode().toLowerCase() + "IntegrationService";
            PlatformIntegrationService integrationService = integrationServices.get(serviceName);
            
            if (integrationService == null) {
                throw new RuntimeException("Platform entegrasyon servisi bulunamadı: " + serviceName);
            }
            
            // Test verisi hazırla
            Map<String, Object> testData = createTestData(platform);
            
            // Test verisini gönder
            boolean success = integrationService.processWebhook(platform, testData);
            
            return Map.of(
                "success", success,
                "message", success ? "Test verisi başarıyla gönderildi" : "Test verisi gönderilemedi",
                "platform", platform.getName(),
                "timestamp", System.currentTimeMillis()
            );
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Test verisi gönderme hatası: " + e.getMessage(),
                "platform", platform.getName(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    private boolean validateEbayCredentials(Platform platform) {
        // eBay API key format kontrolü
        if (!platform.getApiKey().matches("^[A-Za-z0-9_-]{32,}$")) {
            throw new RuntimeException("eBay API Key formatı geçersiz");
        }
        
        // eBay API endpoint kontrolü
        if (!platform.getApiEndpoint().contains("ebay.com")) {
            throw new RuntimeException("eBay API Endpoint geçersiz");
        }
        
        return true;
    }
    
    private boolean validateShopifyCredentials(Platform platform) {
        // Shopify access token format kontrolü
        if (!platform.getAccessToken().matches("^shpat_[A-Za-z0-9]{32,}$")) {
            throw new RuntimeException("Shopify Access Token formatı geçersiz");
        }
        
        // Shopify API endpoint kontrolü
        if (!platform.getApiEndpoint().contains("myshopify.com")) {
            throw new RuntimeException("Shopify API Endpoint geçersiz");
        }
        
        return true;
    }
    
    private boolean validateAmazonCredentials(Platform platform) {
        // Amazon API key format kontrolü
        if (!platform.getApiKey().matches("^AKIA[A-Z0-9]{16}$")) {
            throw new RuntimeException("Amazon API Key formatı geçersiz");
        }
        
        // Amazon API endpoint kontrolü
        if (!platform.getApiEndpoint().contains("amazon.com")) {
            throw new RuntimeException("Amazon API Endpoint geçersiz");
        }
        
        return true;
    }
    
    private boolean validateTrendyolCredentials(Platform platform) {
        // Trendyol API key format kontrolü
        if (platform.getApiKey().length() < 20) {
            throw new RuntimeException("Trendyol API Key çok kısa");
        }
        
        // Trendyol API endpoint kontrolü
        if (!platform.getApiEndpoint().contains("trendyol.com")) {
            throw new RuntimeException("Trendyol API Endpoint geçersiz");
        }
        
        return true;
    }
    
    private Map<String, Object> createTestData(Platform platform) {
        return Map.of(
            "eventType", "TEST_CONNECTION",
            "platform", platform.getCode(),
            "timestamp", System.currentTimeMillis(),
            "testData", Map.of(
                "message", "Bu bir test verisidir",
                "source", "Stok Takip Sistemi"
            )
        );
    }
}

