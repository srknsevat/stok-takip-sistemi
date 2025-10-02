package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.OrderItem;
import com.ornek.stoktakip.dto.OrderProcessingResult;
import java.util.List;
import java.util.Map;

public interface OrderProcessingService {
    
    /**
     * Siparişi işle ve stokları güncelle
     */
    OrderProcessingResult processOrder(Order order);
    
    /**
     * Siparişi iptal et ve stokları geri yükle
     */
    OrderProcessingResult cancelOrder(Order order);
    
    /**
     * Siparişi onayla
     */
    OrderProcessingResult confirmOrder(Order order);
    
    /**
     * Siparişi sevk et
     */
    OrderProcessingResult shipOrder(Order order);
    
    /**
     * Siparişi teslim et
     */
    OrderProcessingResult deliverOrder(Order order);
    
    /**
     * Siparişi iade et
     */
    OrderProcessingResult returnOrder(Order order);
    
    /**
     * Stok kontrolü yap
     */
    boolean checkStockAvailability(Order order);
    
    /**
     * Stok rezervasyonu yap
     */
    boolean reserveStock(Order order);
    
    /**
     * Stok rezervasyonunu iptal et
     */
    boolean releaseReservedStock(Order order);
    
    /**
     * Sipariş durumunu güncelle
     */
    Order updateOrderStatus(Order order, Order.OrderStatus newStatus);
    
    /**
     * Sipariş öğelerini işle
     */
    List<OrderItem> processOrderItems(Order order);
    
    /**
     * BOM patlatma işlemi
     */
    Map<Long, Double> explodeBOMForOrder(Order order);
    
    /**
     * Stok hareketi oluştur
     */
    void createStockMovement(Long materialId, Double quantity, String movementType, String description);
    
    /**
     * Platform stoklarını güncelle
     */
    boolean updatePlatformStocks(Order order);
    
    /**
     * Sipariş işleme geçmişi
     */
    List<OrderProcessingResult> getOrderProcessingHistory(Long orderId);
    
    /**
     * Bekleyen siparişleri işle
     */
    List<OrderProcessingResult> processPendingOrders();
    
    /**
     * Sipariş işleme istatistikleri
     */
    Map<String, Object> getOrderProcessingStats();
}