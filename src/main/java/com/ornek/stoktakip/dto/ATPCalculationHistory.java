package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ATPCalculationHistory {
    private Long id;
    private MaterialCard material;
    private BigDecimal atpQuantity;
    private String calculationMethod;
    private LocalDateTime calculationDate;
    private String notes;
    
    public ATPCalculationHistory() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public BigDecimal getAtpQuantity() { return atpQuantity; }
    public void setAtpQuantity(BigDecimal atpQuantity) { this.atpQuantity = atpQuantity; }
    
    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
    
    public LocalDateTime getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
