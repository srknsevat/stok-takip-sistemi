package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class StockConstraintAnalysis {
    private Product product;
    private BigDecimal currentStock;
    private BigDecimal requiredStock;
    private BigDecimal shortage;
    private String constraintType;
    private String severity;
    private LocalDateTime analysisDate;
    private List<String> recommendations;

    public StockConstraintAnalysis() {
        this.analysisDate = LocalDateTime.now();
    }

    public StockConstraintAnalysis(Product product, BigDecimal currentStock, BigDecimal requiredStock) {
        this.product = product;
        this.currentStock = currentStock;
        this.requiredStock = requiredStock;
        this.shortage = requiredStock.subtract(currentStock);
        this.analysisDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    
    public BigDecimal getRequiredStock() { return requiredStock; }
    public void setRequiredStock(BigDecimal requiredStock) { this.requiredStock = requiredStock; }
    
    public BigDecimal getShortage() { return shortage; }
    public void setShortage(BigDecimal shortage) { this.shortage = shortage; }
    
    public String getConstraintType() { return constraintType; }
    public void setConstraintType(String constraintType) { this.constraintType = constraintType; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}