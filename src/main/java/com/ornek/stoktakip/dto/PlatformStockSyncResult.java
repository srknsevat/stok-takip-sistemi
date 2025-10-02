package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Platform;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlatformStockSyncResult {
    private Platform platform;
    private int totalProducts;
    private int syncedProducts;
    private int failedProducts;
    private LocalDateTime syncDate;
    private String syncStatus;
    private List<String> errors;
    
    public PlatformStockSyncResult() {
        this.errors = new ArrayList<>();
    }
    
    // Getters and Setters
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
    
    public int getSyncedProducts() { return syncedProducts; }
    public void setSyncedProducts(int syncedProducts) { this.syncedProducts = syncedProducts; }
    
    public int getFailedProducts() { return failedProducts; }
    public void setFailedProducts(int failedProducts) { this.failedProducts = failedProducts; }
    
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
