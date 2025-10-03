package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Order;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderStockCheckResult {
    private Order order;
    private boolean stockAvailable;
    private List<StockCheckItem> stockCheckItems;
    private String message;
    private LocalDateTime checkDate;
    
    public OrderStockCheckResult() {
        this.stockCheckItems = new ArrayList<>();
        this.checkDate = LocalDateTime.now();
    }
    
    public OrderStockCheckResult(Order order, boolean stockAvailable) {
        this();
        this.order = order;
        this.stockAvailable = stockAvailable;
    }
    
    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public boolean isStockAvailable() { return stockAvailable; }
    public void setStockAvailable(boolean stockAvailable) { this.stockAvailable = stockAvailable; }
    
    public List<StockCheckItem> getStockCheckItems() { return stockCheckItems; }
    public void setStockCheckItems(List<StockCheckItem> stockCheckItems) { this.stockCheckItems = stockCheckItems; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDateTime checkDate) { this.checkDate = checkDate; }
    
    // Inner class for stock check items
    public static class StockCheckItem {
        private Long productId;
        private String productName;
        private Integer requiredQuantity;
        private Integer availableQuantity;
        private boolean available;
        private String status;
        
        public StockCheckItem() {}
        
        public StockCheckItem(Long productId, String productName, Integer requiredQuantity, Integer availableQuantity) {
            this.productId = productId;
            this.productName = productName;
            this.requiredQuantity = requiredQuantity;
            this.availableQuantity = availableQuantity;
            this.available = availableQuantity >= requiredQuantity;
            this.status = this.available ? "YETERLI" : "YETERSIZ";
        }
        
        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(Integer requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public Integer getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
