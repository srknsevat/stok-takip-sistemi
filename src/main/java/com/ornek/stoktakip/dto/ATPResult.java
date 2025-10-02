package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ATPResult {
    private MaterialCard material;
    private BigDecimal currentStock;
    private BigDecimal availableToPromise;
    private BigDecimal reservedQuantity;
    private BigDecimal allocatedQuantity;
    private BigDecimal safetyStock;
    private BigDecimal reorderPoint;
    private BigDecimal reorderQuantity;
    private List<ATPConstraint> constraints;
    private LocalDateTime calculationDate;
    private String calculationMethod;
    
    public ATPResult() {
        this.constraints = new ArrayList<>();
    }
    
    public ATPResult(MaterialCard material, BigDecimal currentStock, BigDecimal availableToPromise) {
        this.material = material;
        this.currentStock = currentStock;
        this.availableToPromise = availableToPromise;
        this.constraints = new ArrayList<>();
        this.calculationDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    
    public BigDecimal getAvailableToPromise() { return availableToPromise; }
    public void setAvailableToPromise(BigDecimal availableToPromise) { this.availableToPromise = availableToPromise; }
    
    public BigDecimal getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(BigDecimal reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    
    public BigDecimal getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(BigDecimal allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
    
    public BigDecimal getSafetyStock() { return safetyStock; }
    public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
    
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    
    public BigDecimal getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(BigDecimal reorderQuantity) { this.reorderQuantity = reorderQuantity; }
    
    public List<ATPConstraint> getConstraints() { return constraints; }
    public void setConstraints(List<ATPConstraint> constraints) { this.constraints = constraints; }
    
    public LocalDateTime getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
    
    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
    
    public boolean hasConstraints() {
        return constraints != null && !constraints.isEmpty();
    }
    
    public BigDecimal getEffectiveATP() {
        if (availableToPromise == null) return BigDecimal.ZERO;
        if (reservedQuantity == null) return availableToPromise;
        return availableToPromise.subtract(reservedQuantity);
    }
}
