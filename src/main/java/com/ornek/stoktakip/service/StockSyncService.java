package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import java.util.List;
import java.util.Map;

public interface StockSyncService {
    
    /**
     * Tüm platformları senkronize et
     */
    boolean syncAllPlatforms();
    
    /**
     * Belirli bir platformu senkronize et
     */
    boolean syncPlatform(Long platformId);
    
    /**
     * Belirli bir malzemenin tüm platformlardaki stoklarını senkronize et
     */
    boolean syncMaterialStock(Long materialId);
    
    /**
     * Platform stok güncelleme
     */
    boolean updatePlatformStock(PlatformProduct platformProduct, Integer newStock);
    
    /**
     * Senkronizasyon durumunu kontrol et
     */
    boolean isSyncNeeded(Platform platform);
    
    /**
     * Senkronizasyon geçmişini al
     */
    List<Map<String, Object>> getSyncHistory();
    
    /**
     * Senkronizasyon istatistiklerini al
     */
    Map<String, Object> getSyncStats();
    
    /**
     * Hata durumunda senkronizasyonu yeniden dene
     */
    boolean retryFailedSync(Long platformId);
    
    /**
     * Senkronizasyonu durdur
     */
    void stopSync();
    
    /**
     * Senkronizasyonu başlat
     */
    void startSync();
}