package com.ornek.stoktakip.service;

import com.ornek.stoktakip.dto.*;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ATPService {
    
    /**
     * Bir malzemenin ATP (Available to Promise) miktarını hesaplar
     */
    ATPResult calculateATP(Long materialId);
    
    /**
     * Bir malzemenin ATP miktarını BOM'a göre hesaplar
     */
    ATPResult calculateATPFromBOM(Long materialId);
    
    /**
     * Tüm platformlardaki stok miktarını ATP'ye göre günceller
     */
    boolean updateAllPlatformStocks();
    
    /**
     * Belirli bir platformdaki stok miktarını ATP'ye göre günceller
     */
    boolean updatePlatformStock(Long platformId);
    
    /**
     * Belirli bir malzemenin tüm platformlardaki stok miktarını günceller
     */
    boolean updateMaterialStockOnAllPlatforms(Long materialId);
    
    /**
     * ATP hesaplama raporu oluşturur
     */
    ATPReport generateATPReport(Long materialId);
    
    /**
     * Stok kısıtlama analizi yapar
     */
    StockConstraintAnalysis analyzeStockConstraints(Long materialId);
    
    /**
     * Stok kısıtlama önerileri verir
     */
    List<StockConstraintRecommendation> getStockConstraintRecommendations(Long materialId);
    
    /**
     * ATP hesaplama geçmişi
     */
    List<ATPCalculationHistory> getATPCalculationHistory(Long materialId);
    
    /**
     * Platform stok senkronizasyonu
     */
    PlatformStockSyncResult syncPlatformStocks(Long platformId);
}