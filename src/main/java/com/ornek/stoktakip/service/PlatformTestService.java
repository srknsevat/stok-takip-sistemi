package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Platform;
import java.util.Map;

public interface PlatformTestService {
    
    /**
     * Platform bağlantısını test et
     */
    boolean testConnection(Platform platform);
    
    /**
     * API anahtarlarını test et
     */
    boolean testApiKeys(String apiKey, String apiSecret, String platformCode);
    
    /**
     * Platform entegrasyonunu test et
     */
    Map<String, Object> testIntegration(Long platformId);
    
    /**
     * Platform senkronizasyonunu test et
     */
    boolean testSync(Long platformId);
    
    /**
     * Platform webhook'unu test et
     */
    boolean testWebhook(Long platformId);
    
    /**
     * Platform performansını test et
     */
    Map<String, Object> testPerformance(Long platformId);
    
    /**
     * Platform güvenliğini test et
     */
    Map<String, Object> testSecurity(Long platformId);
}