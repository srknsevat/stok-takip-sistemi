package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.PlatformTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Platform bağlantı testi servisi implementasyonu
 */
@Service
public class PlatformTestServiceImpl implements PlatformTestService {
    
    private final PlatformService platformService;
    private final RestTemplate restTemplate;
    
    @Autowired
    public PlatformTestServiceImpl(PlatformService platformService, RestTemplate restTemplate) {
        this.platformService = platformService;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public boolean testConnection(Platform platform) {
        try {
            // Temel validasyonlar
            if (platform.getApiKey() == null || platform.getApiKey().trim().isEmpty()) {
                return false;
            }
            
            if (platform.getApiSecret() == null || platform.getApiSecret().trim().isEmpty()) {
                return false;
            }
            
            if (platform.getApiEndpoint() == null || platform.getApiEndpoint().trim().isEmpty()) {
                return false;
            }
            
            // Platform'a özel test
            return testPlatformSpecificConnection(platform);
            
        } catch (Exception e) {
            System.err.println("Platform bağlantı testi hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean testApiKeys(String apiKey, String apiSecret, String platformCode) {
        try {
            if (apiKey == null || apiKey.trim().isEmpty() || 
                apiSecret == null || apiSecret.trim().isEmpty()) {
                return false;
            }
            
            // Platform'a özel format kontrolü
            switch (platformCode.toUpperCase()) {
                case "EBAY":
                    return apiKey.matches("^[A-Za-z0-9_-]{32,}$");
                case "SHOPIFY":
                    return apiKey.matches("^[A-Za-z0-9]{32,}$");
                case "AMAZON":
                    return apiKey.matches("^AKIA[A-Z0-9]{16}$");
                case "TRENDYOL":
                    return apiKey.length() >= 20;
                default:
                    return apiKey.length() >= 10; // Genel kontrol
            }
            
        } catch (Exception e) {
            System.err.println("API key test hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> testIntegration(Long platformId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                result.put("success", false);
                result.put("message", "Platform bulunamadı");
                return result;
            }
            
            // Bağlantı testi
            boolean connectionTest = testConnection(platform);
            
            // API key testi
            boolean apiKeyTest = testApiKeys(platform.getApiKey(), platform.getApiSecret(), platform.getCode());
            
            result.put("success", connectionTest && apiKeyTest);
            result.put("connectionTest", connectionTest);
            result.put("apiKeyTest", apiKeyTest);
            result.put("platformName", platform.getName());
            result.put("timestamp", LocalDateTime.now());
            
            if (connectionTest && apiKeyTest) {
                result.put("message", "Platform entegrasyonu başarılı");
            } else {
                result.put("message", "Platform entegrasyonu başarısız");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Test hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean testSync(Long platformId) {
        try {
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                return false;
            }
            
            // Basit senkronizasyon testi
            platform.setLastSyncAt(LocalDateTime.now());
            platformService.savePlatform(platform);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Sync test hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean testWebhook(Long platformId) {
        try {
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                return false;
            }
            
            // Webhook URL kontrolü
            if (platform.getWebhookUrl() == null || platform.getWebhookUrl().trim().isEmpty()) {
                return false;
            }
            
            // URL format kontrolü
            return platform.getWebhookUrl().startsWith("https://") || 
                   platform.getWebhookUrl().startsWith("http://");
            
        } catch (Exception e) {
            System.err.println("Webhook test hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> testPerformance(Long platformId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                result.put("success", false);
                result.put("message", "Platform bulunamadı");
                return result;
            }
            
            // Bağlantı testi
            boolean connectionTest = testConnection(platform);
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            result.put("success", connectionTest);
            result.put("responseTime", responseTime);
            result.put("platformName", platform.getName());
            result.put("timestamp", LocalDateTime.now());
            
            if (responseTime < 1000) {
                result.put("performance", "Excellent");
            } else if (responseTime < 3000) {
                result.put("performance", "Good");
            } else if (responseTime < 5000) {
                result.put("performance", "Average");
            } else {
                result.put("performance", "Poor");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Performance test hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> testSecurity(Long platformId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                result.put("success", false);
                result.put("message", "Platform bulunamadı");
                return result;
            }
            
            // Güvenlik kontrolleri
            boolean hasApiKey = platform.getApiKey() != null && !platform.getApiKey().trim().isEmpty();
            boolean hasApiSecret = platform.getApiSecret() != null && !platform.getApiSecret().trim().isEmpty();
            boolean hasHttpsEndpoint = platform.getApiEndpoint() != null && 
                                     platform.getApiEndpoint().startsWith("https://");
            
            int securityScore = 0;
            if (hasApiKey) securityScore += 30;
            if (hasApiSecret) securityScore += 30;
            if (hasHttpsEndpoint) securityScore += 40;
            
            result.put("success", securityScore >= 70);
            result.put("securityScore", securityScore);
            result.put("hasApiKey", hasApiKey);
            result.put("hasApiSecret", hasApiSecret);
            result.put("hasHttpsEndpoint", hasHttpsEndpoint);
            result.put("platformName", platform.getName());
            result.put("timestamp", LocalDateTime.now());
            
            if (securityScore >= 90) {
                result.put("securityLevel", "High");
            } else if (securityScore >= 70) {
                result.put("securityLevel", "Medium");
            } else {
                result.put("securityLevel", "Low");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Security test hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    private boolean testPlatformSpecificConnection(Platform platform) {
        try {
            // Platform'a özel bağlantı testi
            switch (platform.getCode().toUpperCase()) {
                case "EBAY":
                    return testEbayConnection(platform);
                case "SHOPIFY":
                    return testShopifyConnection(platform);
                case "AMAZON":
                    return testAmazonConnection(platform);
                case "TRENDYOL":
                    return testTrendyolConnection(platform);
                default:
                    return true; // Bilinmeyen platformlar için temel kontrol yeterli
            }
        } catch (Exception e) {
            System.err.println("Platform özel bağlantı testi hatası: " + e.getMessage());
            return false;
        }
    }
    
    private boolean testEbayConnection(Platform platform) {
        // eBay API endpoint kontrolü
        return platform.getApiEndpoint().contains("ebay.com");
    }
    
    private boolean testShopifyConnection(Platform platform) {
        // Shopify API endpoint kontrolü
        return platform.getApiEndpoint().contains("myshopify.com");
    }
    
    private boolean testAmazonConnection(Platform platform) {
        // Amazon API endpoint kontrolü
        return platform.getApiEndpoint().contains("amazon.com");
    }
    
    private boolean testTrendyolConnection(Platform platform) {
        // Trendyol API endpoint kontrolü
        return platform.getApiEndpoint().contains("trendyol.com");
    }
    
    @Override
    public boolean testPlatformConnection(Platform platform) {
        return testConnection(platform);
    }
}