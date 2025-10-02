package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StockConstraintAnalysis {
    private MaterialCard material;
    private BigDecimal currentATP;
    private BigDecimal maxPossibleATP;
    private List<ATPConstraint> primaryConstraints;
    private List<ATPConstraint> secondaryConstraints;
    private String analysisSummary;
    private LocalDateTime analysisDate;
    
    public StockConstraintAnalysis() {
        this.primaryConstraints = new ArrayList<>();
        this.secondaryConstraints = new ArrayList<>();
    }
    
    // Getters and Setters
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public BigDecimal getCurrentATP() { return currentATP; }
    public void setCurrentATP(BigDecimal currentATP) { this.currentATP = currentATP; }
    
    public BigDecimal getMaxPossibleATP() { return maxPossibleATP; }
    public void setMaxPossibleATP(BigDecimal maxPossibleATP) { this.maxPossibleATP = maxPossibleATP; }
    
    public List<ATPConstraint> getPrimaryConstraints() { return primaryConstraints; }
    public void setPrimaryConstraints(List<ATPConstraint> primaryConstraints) { this.primaryConstraints = primaryConstraints; }
    
    public List<ATPConstraint> getSecondaryConstraints() { return secondaryConstraints; }
    public void setSecondaryConstraints(List<ATPConstraint> secondaryConstraints) { this.secondaryConstraints = secondaryConstraints; }
    
    public String getAnalysisSummary() { return analysisSummary; }
    public void setAnalysisSummary(String analysisSummary) { this.analysisSummary = analysisSummary; }
    
    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }
}