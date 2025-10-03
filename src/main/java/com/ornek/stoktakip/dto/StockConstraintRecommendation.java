package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockConstraintRecommendation {
    private Product product;
    private String recommendationType;
    private String description;
    private BigDecimal suggestedQuantity;
    private BigDecimal estimatedCost;
    private String priority;
    private LocalDateTime recommendedDate;
    private String status;

    public StockConstraintRecommendation() {
        this.recommendedDate = LocalDateTime.now();
    }

    public StockConstraintRecommendation(Product product, String recommendationType, String description) {
        this.product = product;
        this.recommendationType = recommendationType;
        this.description = description;
        this.recommendedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(BigDecimal suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
    
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDateTime getRecommendedDate() { return recommendedDate; }
    public void setRecommendedDate(LocalDateTime recommendedDate) { this.recommendedDate = recommendedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}