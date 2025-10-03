package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Platform;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PlatformStockSyncResult {
    private Platform platform;
    private boolean success;
    private String message;
    private LocalDateTime syncTime;
    private int totalProducts;
    private int successfulUpdates;
    private int failedUpdates;
    private List<String> errors;
    private Map<String, String> updateDetails;

    public PlatformStockSyncResult() {
        this.syncTime = LocalDateTime.now();
    }

    public PlatformStockSyncResult(Platform platform, boolean success, String message) {
        this.platform = platform;
        this.success = success;
        this.message = message;
        this.syncTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getSyncTime() { return syncTime; }
    public void setSyncTime(LocalDateTime syncTime) { this.syncTime = syncTime; }
    
    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
    
    public int getSuccessfulUpdates() { return successfulUpdates; }
    public void setSuccessfulUpdates(int successfulUpdates) { this.successfulUpdates = successfulUpdates; }
    
    public int getFailedUpdates() { return failedUpdates; }
    public void setFailedUpdates(int failedUpdates) { this.failedUpdates = failedUpdates; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public Map<String, String> getUpdateDetails() { return updateDetails; }
    public void setUpdateDetails(Map<String, String> updateDetails) { this.updateDetails = updateDetails; }
}