package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockConstraintRecommendation {
    private MaterialCard material;
    private String recommendationType;
    private String description;
    private BigDecimal suggestedQuantity;
    private BigDecimal expectedImprovement;
    private String priority;
    private LocalDateTime recommendedDate;
    
    public StockConstraintRecommendation() {}
    
    // Getters and Setters
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(BigDecimal suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
    
    public BigDecimal getExpectedImprovement() { return expectedImprovement; }
    public void setExpectedImprovement(BigDecimal expectedImprovement) { this.expectedImprovement = expectedImprovement; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public LocalDateTime getRecommendedDate() { return recommendedDate; }
    public void setRecommendedDate(LocalDateTime recommendedDate) { this.recommendedDate = recommendedDate; }
}
