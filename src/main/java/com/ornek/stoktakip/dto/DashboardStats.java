package com.ornek.stoktakip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DashboardStats {
    
    private long totalMaterials;
    private long totalStock;
    private BigDecimal totalValue;
    private long activePlatforms;
    private long totalUsers;
    private long totalOrders;
    private long pendingOrders;
    private long lowStockMaterials;
    private LocalDateTime lastSync;
    private String systemStatus;
    
    public DashboardStats() {}
    
    public DashboardStats(long totalMaterials, long totalStock, BigDecimal totalValue, long activePlatforms) {
        this.totalMaterials = totalMaterials;
        this.totalStock = totalStock;
        this.totalValue = totalValue;
        this.activePlatforms = activePlatforms;
    }
    
    // Getters and Setters
    public long getTotalMaterials() { return totalMaterials; }
    public void setTotalMaterials(long totalMaterials) { this.totalMaterials = totalMaterials; }
    
    public long getTotalStock() { return totalStock; }
    public void setTotalStock(long totalStock) { this.totalStock = totalStock; }
    
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    
    public long getActivePlatforms() { return activePlatforms; }
    public void setActivePlatforms(long activePlatforms) { this.activePlatforms = activePlatforms; }
    
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    
    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    
    public long getLowStockMaterials() { return lowStockMaterials; }
    public void setLowStockMaterials(long lowStockMaterials) { this.lowStockMaterials = lowStockMaterials; }
    
    public LocalDateTime getLastSync() { return lastSync; }
    public void setLastSync(LocalDateTime lastSync) { this.lastSync = lastSync; }
    
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
}
