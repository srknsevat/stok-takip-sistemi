package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;

public class ATPConstraint {
    private MaterialCard constraintMaterial;
    private BigDecimal requiredQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal constraintQuantity;
    private Integer bomLevel;
    private String bomPath;
    private String constraintType; // STOCK, LEAD_TIME, QUALITY, etc.
    private String description;
    
    public ATPConstraint() {}
    
    public ATPConstraint(MaterialCard constraintMaterial, BigDecimal requiredQuantity, BigDecimal availableQuantity) {
        this.constraintMaterial = constraintMaterial;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
        this.constraintQuantity = availableQuantity.divide(requiredQuantity, 0, BigDecimal.ROUND_DOWN);
    }
    
    // Getters and Setters
    public MaterialCard getConstraintMaterial() { return constraintMaterial; }
    public void setConstraintMaterial(MaterialCard constraintMaterial) { this.constraintMaterial = constraintMaterial; }
    
    public BigDecimal getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
    
    public BigDecimal getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; }
    
    public BigDecimal getConstraintQuantity() { return constraintQuantity; }
    public void setConstraintQuantity(BigDecimal constraintQuantity) { this.constraintQuantity = constraintQuantity; }
    
    public Integer getBomLevel() { return bomLevel; }
    public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
    
    public String getBomPath() { return bomPath; }
    public void setBomPath(String bomPath) { this.bomPath = bomPath; }
    
    public String getConstraintType() { return constraintType; }
    public void setConstraintType(String constraintType) { this.constraintType = constraintType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isConstraint() {
        return constraintQuantity != null && constraintQuantity.compareTo(BigDecimal.ZERO) > 0;
    }
}
