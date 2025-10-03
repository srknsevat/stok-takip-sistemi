package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.OrderItem;
import com.ornek.stoktakip.dto.OrderProcessingResult;
import com.ornek.stoktakip.dto.OrderStockCheckResult;
import com.ornek.stoktakip.dto.OrderCostResult;
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
    
    /**
     * Sipariş üretim planı oluştur
     */
    OrderProductionPlan createOrderProductionPlan(Order order);
    
    /**
     * Sipariş stok kontrolü yap
     */
    OrderStockCheckResult checkOrderStock(Order order);
    
    /**
     * Sipariş maliyet hesapla
     */
    OrderCostResult calculateOrderCost(Order order);
    
    /**
     * BOM ile sipariş işle
     */
    OrderProcessingResult processOrderWithBOM(Order order);
    
    // Eksik metodlar
    OrderProcessingReport generateOrderProcessingReport(Order order);
    
    /**
     * Sipariş üretim planı sınıfı
     */
    class OrderProductionPlan {
        private Order order;
        private List<MrpItem> mrpItems;
        private String status;
        private String notes;
        
        public OrderProductionPlan() {}
        
        public OrderProductionPlan(Order order, List<MrpItem> mrpItems, String status) {
            this.order = order;
            this.mrpItems = mrpItems;
            this.status = status;
        }
        
        // Getters and Setters
        public Order getOrder() { return order; }
        public void setOrder(Order order) { this.order = order; }
        
        public List<MrpItem> getMrpItems() { return mrpItems; }
        public void setMrpItems(List<MrpItem> mrpItems) { this.mrpItems = mrpItems; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    /**
     * MRP Item sınıfı
     */
    class MrpItem {
        private Long materialId;
        private String materialName;
        private Double requiredQuantity;
        private Double availableQuantity;
        private Double shortageQuantity;
        private String unit;
        private String status;
        
        public MrpItem() {}
        
        public MrpItem(Long materialId, String materialName, Double requiredQuantity, Double availableQuantity) {
            this.materialId = materialId;
            this.materialName = materialName;
            this.requiredQuantity = requiredQuantity;
            this.availableQuantity = availableQuantity;
            this.shortageQuantity = Math.max(0, requiredQuantity - availableQuantity);
        }
        
        // Getters and Setters
        public Long getMaterialId() { return materialId; }
        public void setMaterialId(Long materialId) { this.materialId = materialId; }
        
        public String getMaterialName() { return materialName; }
        public void setMaterialName(String materialName) { this.materialName = materialName; }
        
        public Double getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public Double getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(Double availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public Double getShortageQuantity() { return shortageQuantity; }
        public void setShortageQuantity(Double shortageQuantity) { this.shortageQuantity = shortageQuantity; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}