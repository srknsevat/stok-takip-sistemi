package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderCostResult {
    private Order order;
    private BigDecimal totalCost;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal overheadCost;
    private BigDecimal profit;
    private BigDecimal profitMargin;
    private List<CostItem> costItems;
    private LocalDateTime calculationDate;
    
    public OrderCostResult() {
        this.costItems = new ArrayList<>();
        this.calculationDate = LocalDateTime.now();
        this.totalCost = BigDecimal.ZERO;
        this.materialCost = BigDecimal.ZERO;
        this.laborCost = BigDecimal.ZERO;
        this.overheadCost = BigDecimal.ZERO;
        this.profit = BigDecimal.ZERO;
        this.profitMargin = BigDecimal.ZERO;
    }
    
    public OrderCostResult(Order order) {
        this();
        this.order = order;
    }
    
    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public BigDecimal getMaterialCost() { return materialCost; }
    public void setMaterialCost(BigDecimal materialCost) { this.materialCost = materialCost; }
    
    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }
    
    public BigDecimal getOverheadCost() { return overheadCost; }
    public void setOverheadCost(BigDecimal overheadCost) { this.overheadCost = overheadCost; }
    
    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }
    
    public BigDecimal getProfitMargin() { return profitMargin; }
    public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }
    
    public List<CostItem> getCostItems() { return costItems; }
    public void setCostItems(List<CostItem> costItems) { this.costItems = costItems; }
    
    public LocalDateTime getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
    
    // Inner class for cost items
    public static class CostItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String costType; // MATERIAL, LABOR, OVERHEAD
        
        public CostItem() {}
        
        public CostItem(Long productId, String productName, Integer quantity, BigDecimal unitCost, String costType) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitCost = unitCost;
            this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
            this.costType = costType;
        }
        
        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public String getCostType() { return costType; }
        public void setCostType(String costType) { this.costType = costType; }
    }
}
