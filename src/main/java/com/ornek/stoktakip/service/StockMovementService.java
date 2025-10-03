package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.StockMovement;
import com.ornek.stoktakip.entity.MaterialCard;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StockMovementService {
    
    /**
     * Stok hareketi oluştur
     */
    StockMovement createStockMovement(StockMovement stockMovement);
    
    /**
     * Tüm stok hareketlerini getir
     */
    List<StockMovement> getAllStockMovements();
    
    /**
     * Malzeme ID'sine göre stok hareketlerini getir
     */
    List<StockMovement> getStockMovementsByMaterialId(Long materialId);
    
    /**
     * Tarih aralığına göre stok hareketlerini getir
     */
    List<StockMovement> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Stok hareketi türüne göre getir
     */
    List<StockMovement> getStockMovementsByType(String movementType);
    
    /**
     * Stok hareketi oluştur (malzeme, miktar, tür ile)
     */
    StockMovement createStockMovement(MaterialCard material, Integer quantity, String movementType, String description);
    
    /**
     * Stok hareketi güncelle
     */
    StockMovement updateStockMovement(StockMovement stockMovement);
    
    /**
     * Stok hareketi sil
     */
    void deleteStockMovement(Long id);
    
    /**
     * Stok hareketi istatistikleri
     */
    Map<String, Object> getStockMovementStats();
    
    /**
     * Malzeme stok geçmişi
     */
    List<StockMovement> getMaterialStockHistory(Long materialId);
    
    /**
     * Stok hareketi raporu
     */
    List<Map<String, Object>> getStockMovementReport(LocalDateTime startDate, LocalDateTime endDate);
    
    // Eksik metodlar
    List<StockMovement> getAllMovements();
    void saveMovement(StockMovement movement);
}