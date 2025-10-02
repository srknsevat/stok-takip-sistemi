package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.OrderItem;
import com.ornek.stoktakip.service.BomExplosionService.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderProcessingService {
    
    /**
     * Siparişi işle ve stoktan düş
     */
    OrderProcessingResult processOrder(Order order);
    
    /**
     * Siparişi işle ve BOM'u patlat
     */
    OrderProcessingResult processOrderWithBOM(Order order);
    
    /**
     * Siparişi işle ve stok kontrolü yap
     */
    OrderStockCheckResult checkOrderStock(Order order);
    
    /**
     * Siparişi işle ve eksik stokları listele
     */
    List<OrderStockShortage> getOrderStockShortages(Order order);
    
    /**
     * Siparişi işle ve maliyet hesapla
     */
    OrderCostResult calculateOrderCost(Order order);
    
    /**
     * Siparişi işle ve üretim planı oluştur
     */
    OrderProductionPlan createOrderProductionPlan(Order order);
    
    /**
     * Siparişi işle ve stok hareketleri oluştur
     */
    List<OrderStockMovement> createOrderStockMovements(Order order);
    
    /**
     * Siparişi işle ve stok hareketlerini commit et
     */
    boolean commitOrderStockMovements(Order order, List<OrderStockMovement> stockMovements);
    
    /**
     * Siparişi işle ve stok hareketlerini rollback et
     */
    boolean rollbackOrderStockMovements(Order order, List<OrderStockMovement> stockMovements);
    
    /**
     * Siparişi işle ve rapor oluştur
     */
    OrderProcessingReport generateOrderProcessingReport(Order order);
    
    // Inner Classes
    class OrderProcessingResult {
        private boolean success;
        private String message;
        private Order order;
        private List<OrderStockMovement> stockMovements;
        private BomExplosionReport bomReport;
        private OrderCostResult costResult;
        private LocalDateTime processingDate;
        
        public OrderProcessingResult() {}
        
        public OrderProcessingResult(boolean success, String message, Order order) {
            this.success = success;
            this.message = message;
            this.order = order;
            this.processingDate = LocalDateTime.now();
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Order getOrder() { return order; }
        public void setOrder(Order order) { this.order = order; }
        
        public List<OrderStockMovement> getStockMovements() { return stockMovements; }
        public void setStockMovements(List<OrderStockMovement> stockMovements) { this.stockMovements = stockMovements; }
        
        public BomExplosionReport getBomReport() { return bomReport; }
        public void setBomReport(BomExplosionReport bomReport) { this.bomReport = bomReport; }
        
        public OrderCostResult getCostResult() { return costResult; }
        public void setCostResult(OrderCostResult costResult) { this.costResult = costResult; }
        
        public LocalDateTime getProcessingDate() { return processingDate; }
        public void setProcessingDate(LocalDateTime processingDate) { this.processingDate = processingDate; }
    }
    
    class OrderStockCheckResult {
        private boolean stockAvailable;
        private List<OrderStockShortage> shortages;
        private BigDecimal totalShortageValue;
        private String message;
        
        public OrderStockCheckResult() {}
        
        public OrderStockCheckResult(boolean stockAvailable, List<OrderStockShortage> shortages) {
            this.stockAvailable = stockAvailable;
            this.shortages = shortages;
        }
        
        // Getters and Setters
        public boolean isStockAvailable() { return stockAvailable; }
        public void setStockAvailable(boolean stockAvailable) { this.stockAvailable = stockAvailable; }
        
        public List<OrderStockShortage> getShortages() { return shortages; }
        public void setShortages(List<OrderStockShortage> shortages) { this.shortages = shortages; }
        
        public BigDecimal getTotalShortageValue() { return totalShortageValue; }
        public void setTotalShortageValue(BigDecimal totalShortageValue) { this.totalShortageValue = totalShortageValue; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    class OrderStockShortage {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private BigDecimal availableStock;
        private BigDecimal shortage;
        private BigDecimal unitCost;
        private BigDecimal shortageValue;
        private String orderItemCode;
        private Integer bomLevel;
        private String bomPath;
        
        public OrderStockShortage() {}
        
        public OrderStockShortage(MaterialCard material, BigDecimal requiredQuantity, BigDecimal availableStock, BigDecimal shortage) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.availableStock = availableStock;
            this.shortage = shortage;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public BigDecimal getAvailableStock() { return availableStock; }
        public void setAvailableStock(BigDecimal availableStock) { this.availableStock = availableStock; }
        
        public BigDecimal getShortage() { return shortage; }
        public void setShortage(BigDecimal shortage) { this.shortage = shortage; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public BigDecimal getShortageValue() { return shortageValue; }
        public void setShortageValue(BigDecimal shortageValue) { this.shortageValue = shortageValue; }
        
        public String getOrderItemCode() { return orderItemCode; }
        public void setOrderItemCode(String orderItemCode) { this.orderItemCode = orderItemCode; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
    }
    
    class OrderCostResult {
        private BigDecimal totalCost;
        private BigDecimal totalQuantity;
        private BigDecimal unitCost;
        private Map<String, BigDecimal> costByItem;
        private Map<String, BigDecimal> costByLevel;
        
        public OrderCostResult() {
            this.costByItem = new HashMap<>();
            this.costByLevel = new HashMap<>();
        }
        
        // Getters and Setters
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public BigDecimal getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(BigDecimal totalQuantity) { this.totalQuantity = totalQuantity; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public Map<String, BigDecimal> getCostByItem() { return costByItem; }
        public void setCostByItem(Map<String, BigDecimal> costByItem) { this.costByItem = costByItem; }
        
        public Map<String, BigDecimal> getCostByLevel() { return costByLevel; }
        public void setCostByLevel(Map<String, BigDecimal> costByLevel) { this.costByLevel = costByLevel; }
    }
    
    class OrderProductionPlan {
        private Order order;
        private List<BomMRPItem> mrpItems;
        private BigDecimal totalCost;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String planStatus;
        
        public OrderProductionPlan() {
            this.mrpItems = new ArrayList<>();
        }
        
        // Getters and Setters
        public Order getOrder() { return order; }
        public void setOrder(Order order) { this.order = order; }
        
        public List<BomMRPItem> getMrpItems() { return mrpItems; }
        public void setMrpItems(List<BomMRPItem> mrpItems) { this.mrpItems = mrpItems; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public String getPlanStatus() { return planStatus; }
        public void setPlanStatus(String planStatus) { this.planStatus = planStatus; }
    }
    
    class OrderStockMovement {
        private MaterialCard material;
        private BigDecimal quantity;
        private String movementType;
        private String referenceNumber;
        private String referenceType;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String reason;
        private LocalDateTime movementDate;
        private String orderItemCode;
        private Integer bomLevel;
        private String bomPath;
        
        public OrderStockMovement() {}
        
        public OrderStockMovement(MaterialCard material, BigDecimal quantity, String movementType) {
            this.material = material;
            this.quantity = quantity;
            this.movementType = movementType;
            this.movementDate = LocalDateTime.now();
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        
        public String getMovementType() { return movementType; }
        public void setMovementType(String movementType) { this.movementType = movementType; }
        
        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
        
        public String getReferenceType() { return referenceType; }
        public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public LocalDateTime getMovementDate() { return movementDate; }
        public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
        
        public String getOrderItemCode() { return orderItemCode; }
        public void setOrderItemCode(String orderItemCode) { this.orderItemCode = orderItemCode; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
    }
    
    class OrderProcessingReport {
        private Order order;
        private LocalDateTime processingDate;
        private OrderProcessingResult processingResult;
        private BomExplosionReport bomReport;
        private OrderCostResult costResult;
        private OrderStockCheckResult stockCheckResult;
        private String reportSummary;
        
        public OrderProcessingReport() {}
        
        // Getters and Setters
        public Order getOrder() { return order; }
        public void setOrder(Order order) { this.order = order; }
        
        public LocalDateTime getProcessingDate() { return processingDate; }
        public void setProcessingDate(LocalDateTime processingDate) { this.processingDate = processingDate; }
        
        public OrderProcessingResult getProcessingResult() { return processingResult; }
        public void setProcessingResult(OrderProcessingResult processingResult) { this.processingResult = processingResult; }
        
        public BomExplosionReport getBomReport() { return bomReport; }
        public void setBomReport(BomExplosionReport bomReport) { this.bomReport = bomReport; }
        
        public OrderCostResult getCostResult() { return costResult; }
        public void setCostResult(OrderCostResult costResult) { this.costResult = costResult; }
        
        public OrderStockCheckResult getStockCheckResult() { return stockCheckResult; }
        public void setStockCheckResult(OrderStockCheckResult stockCheckResult) { this.stockCheckResult = stockCheckResult; }
        
        public String getReportSummary() { return reportSummary; }
        public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }
    }
}
