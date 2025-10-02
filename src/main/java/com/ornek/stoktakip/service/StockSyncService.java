package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.Product;

/**
 * Stok senkronizasyon servisi interface'i
 */
public interface StockSyncService {
    
    /**
     * Tüm platformlarda stok senkronizasyonu
     */
    void syncAllPlatforms();
    
    /**
     * Belirli bir platformda stok senkronizasyonu
     */
    void syncPlatform(Platform platform);
    
    /**
     * Belirli bir ürünün tüm platformlarda senkronizasyonu
     */
    void syncProduct(Product product);
    
    /**
     * Stok güncelleme işlemi (satış sonrası)
     */
    void updateStockAfterSale(Product product, Integer soldQuantity);
    
    /**
     * Stok geri yükleme işlemi (iade sonrası)
     */
    void restoreStockAfterReturn(Product product, Integer returnedQuantity);
    
    /**
     * Manuel stok güncelleme
     */
    void updateStockManually(Product product, Integer newStockQuantity);
    
    /**
     * Başarısız senkronizasyonları tekrar dene
     */
    void retryFailedSyncs();
    
    /**
     * Platform'lar arası stok tutarlılığını kontrol et
     */
    void checkStockConsistency();
    
    /**
     * Gerçek zamanlı stok durumunu hesapla
     */
    Integer calculateRealTimeStock(Product product);
    
    /**
     * Stok senkronizasyon durumunu kontrol et
     */
    boolean isStockSynced(Product product);
    
    /**
     * Platform'da stok güncelleme
     */
    boolean updateStockOnPlatform(Platform platform, Product product, Integer newStockQuantity);
}
