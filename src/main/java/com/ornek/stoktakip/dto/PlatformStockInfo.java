package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlatformStockInfo {
    private Platform platform;
    private PlatformProduct platformProduct;
    private BigDecimal currentStock;
    private BigDecimal atpStock;
    private BigDecimal reservedStock;
    private Integer oldStock;
    private LocalDateTime lastSync;
    private String syncStatus;
    
    public PlatformStockInfo() {}
    
    // Getters and Setters
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public PlatformProduct getPlatformProduct() { return platformProduct; }
    public void setPlatformProduct(PlatformProduct platformProduct) { this.platformProduct = platformProduct; }
    
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    
    public BigDecimal getAtpStock() { return atpStock; }
    public void setAtpStock(BigDecimal atpStock) { this.atpStock = atpStock; }
    
    public BigDecimal getReservedStock() { return reservedStock; }
    public void setReservedStock(BigDecimal reservedStock) { this.reservedStock = reservedStock; }
    
    public LocalDateTime getLastSync() { return lastSync; }
    public void setLastSync(LocalDateTime lastSync) { this.lastSync = lastSync; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public Integer getOldStock() { return oldStock; }
    public void setOldStock(Integer oldStock) { this.oldStock = oldStock; }
}
