package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ATPCalculationHistory {
    private Long id;
    private Product product;
    private BigDecimal calculatedATP;
    private BigDecimal previousATP;
    private String calculationType;
    private LocalDateTime calculationDate;
    private String status;
    private String notes;

    public ATPCalculationHistory() {
        this.calculationDate = LocalDateTime.now();
    }

    public ATPCalculationHistory(Product product, BigDecimal calculatedATP, String calculationType) {
        this.product = product;
        this.calculatedATP = calculatedATP;
        this.calculationType = calculationType;
        this.calculationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public BigDecimal getCalculatedATP() { return calculatedATP; }
    public void setCalculatedATP(BigDecimal calculatedATP) { this.calculatedATP = calculatedATP; }
    
    public BigDecimal getPreviousATP() { return previousATP; }
    public void setPreviousATP(BigDecimal previousATP) { this.previousATP = previousATP; }
    
    public String getCalculationType() { return calculationType; }
    public void setCalculationType(String calculationType) { this.calculationType = calculationType; }
    
    public LocalDateTime getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}