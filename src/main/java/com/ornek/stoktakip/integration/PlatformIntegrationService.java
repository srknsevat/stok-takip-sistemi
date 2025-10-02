package com.ornek.stoktakip.integration;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * E-ticaret platform entegrasyonları için temel interface
 */
public interface PlatformIntegrationService {
    
    /**
     * Platform'a ürün oluşturma
     */
    PlatformProduct createProduct(Platform platform, Product product);
    
    /**
     * Platform'da ürün güncelleme
     */
    PlatformProduct updateProduct(Platform platform, Product product, PlatformProduct platformProduct);
    
    /**
     * Platform'dan ürün silme
     */
    boolean deleteProduct(Platform platform, PlatformProduct platformProduct);
    
    /**
     * Platform'da stok güncelleme
     */
    boolean updateStock(Platform platform, Product product, Integer newStockQuantity);
    
    /**
     * Platform'dan ürün bilgilerini çekme
     */
    PlatformProduct getProductFromPlatform(Platform platform, String platformProductId);
    
    /**
     * Platform'dan tüm ürünleri çekme
     */
    List<PlatformProduct> getAllProductsFromPlatform(Platform platform);
    
    /**
     * Platform bağlantısını test etme
     */
    boolean testConnection(Platform platform);
    
    /**
     * Platform'dan siparişleri çekme
     */
    List<Map<String, Object>> getOrdersFromPlatform(Platform platform, String startDate, String endDate);
    
    /**
     * Platform'a sipariş durumu güncelleme
     */
    boolean updateOrderStatus(Platform platform, String platformOrderId, String newStatus);
    
    /**
     * Platform webhook'larını işleme
     */
    boolean processWebhook(Platform platform, Map<String, Object> webhookData);
    
    /**
     * Platform'a özel ürün verilerini hazırlama
     */
    Map<String, Object> prepareProductData(Platform platform, Product product);
    
    /**
     * Platform'dan gelen veriyi işleme
     */
    Product processPlatformProductData(Platform platform, Map<String, Object> platformData);
    
    /**
     * Platform adını döndürme
     */
    String getPlatformName();
    
    /**
     * Platform kodunu döndürme
     */
    String getPlatformCode();
    
    /**
     * Platform API versiyonunu döndürme
     */
    String getApiVersion();
    
    /**
     * Platform'a özel hata mesajlarını işleme
     */
    String processErrorMessage(Platform platform, Exception exception);
    
    /**
     * Platform'a özel rate limit kontrolü
     */
    boolean checkRateLimit(Platform platform);
    
    /**
     * Platform'a özel retry mekanizması
     */
    boolean retryOperation(Platform platform, Runnable operation, int maxRetries);
}
