package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Platform;
import java.util.List;
import java.util.Map;

public interface StockSyncService {
    
    /**
     * Gerçek zamanlı stok hesaplama
     */
    Integer calculateRealTimeStock(MaterialCard product);
    
    /**
     * Stok senkronizasyon durumu kontrolü
     */
    boolean isStockSynced(MaterialCard product);
    
    /**
     * Platform senkronizasyonu
     */
    void syncPlatform(Platform platform);
    
    /**
     * Tüm platformları senkronize et
     */
    void syncAllPlatforms();
    
    /**
     * Belirli bir ürünü tüm platformlarda senkronize et
     */
    void syncProductOnAllPlatforms(MaterialCard product);
    
    /**
     * Senkronizasyon durumu raporu
     */
    Map<String, Object> getSyncStatus();
    
    /**
     * Başarısız senkronizasyonları getir
     */
    List<Map<String, Object>> getFailedSyncs();
    
    /**
     * Platform senkronizasyon durumları
     */
    List<Map<String, Object>> getPlatformSyncStatus();
    
    /**
     * Senkronizasyon geçmişi
     */
    List<Map<String, Object>> getSyncHistory(Long platformId);
    
    /**
     * Senkronizasyon istatistikleri
     */
    Map<String, Object> getSyncStatistics();
}