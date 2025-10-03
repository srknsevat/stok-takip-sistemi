package com.ornek.stoktakip.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class BomExplosionResult {
    
    private Long materialId;
    private String materialCode;
    private String materialName;
    private BigDecimal requiredQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal shortageQuantity;
    private String unit;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private boolean isAvailable;
    private String status;
    private LocalDateTime calculatedAt;
    private Map<String, Object> additionalInfo;
    
    // Constructors
    public BomExplosionResult() {}
    
    public BomExplosionResult(Long materialId, String materialCode, String materialName, 
                             BigDecimal requiredQuantity, BigDecimal availableQuantity, String unit) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
        this.unit = unit;
        this.calculatedAt = LocalDateTime.now();
        calculateShortage();
    }
    
    // Helper methods
    private void calculateShortage() {
        if (requiredQuantity != null && availableQuantity != null) {
            this.shortageQuantity = requiredQuantity.subtract(availableQuantity);
            this.isAvailable = shortageQuantity.compareTo(BigDecimal.ZERO) <= 0;
            this.status = isAvailable ? "AVAILABLE" : "SHORTAGE";
        }
    }
    
    // Getters and Setters
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public BigDecimal getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(BigDecimal requiredQuantity) { 
        this.requiredQuantity = requiredQuantity; 
        calculateShortage();
    }
    
    public BigDecimal getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(BigDecimal availableQuantity) { 
        this.availableQuantity = availableQuantity; 
        calculateShortage();
    }
    
    public BigDecimal getShortageQuantity() { return shortageQuantity; }
    public void setShortageQuantity(BigDecimal shortageQuantity) { this.shortageQuantity = shortageQuantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
    
    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
}
