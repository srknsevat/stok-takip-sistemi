package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderProcessingService {
    
    /**
     * Sipariş işleme
     */
    boolean processOrder(Long materialId, BigDecimal quantity, String orderType);
    
    /**
     * Sipariş iptal etme
     */
    boolean cancelOrder(Long materialId, BigDecimal quantity, String orderType);
    
    /**
     * Sipariş iade etme
     */
    boolean returnOrder(Long materialId, BigDecimal quantity, String orderType);
    
    /**
     * Stok hareketi oluşturma
     */
    boolean createStockMovement(Long materialId, BigDecimal quantity, String movementType, String description);
    
    /**
     * BOM patlatma ve stok düşürme
     */
    boolean explodeBOMAndDeductStock(Long materialId, BigDecimal quantity);
    
    /**
     * Stok kontrolü
     */
    boolean checkStockAvailability(Long materialId, BigDecimal quantity);
    
    /**
     * Sipariş geçmişi
     */
    List<Map<String, Object>> getOrderHistory(Long materialId);
    
    /**
     * Stok hareket geçmişi
     */
    List<Map<String, Object>> getStockMovementHistory(Long materialId);
}