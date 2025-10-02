package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderProcessingResult {
    
    private Order order;
    private boolean success;
    private String message;
    private LocalDateTime processedAt;
    private String processingType; // PROCESS, CANCEL, CONFIRM, SHIP, DELIVER, RETURN
    private List<String> errors;
    private List<String> warnings;
    private Map<String, Object> additionalData;
    
    public OrderProcessingResult() {
        this.processedAt = LocalDateTime.now();
        this.errors = new java.util.ArrayList<>();
        this.warnings = new java.util.ArrayList<>();
        this.additionalData = new java.util.HashMap<>();
    }
    
    public OrderProcessingResult(Order order, boolean success, String message) {
        this();
        this.order = order;
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public String getProcessingType() { return processingType; }
    public void setProcessingType(String processingType) { this.processingType = processingType; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public Map<String, Object> getAdditionalData() { return additionalData; }
    public void setAdditionalData(Map<String, Object> additionalData) { this.additionalData = additionalData; }
    
    // Helper methods
    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new java.util.ArrayList<>();
        }
        this.errors.add(error);
    }
    
    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new java.util.ArrayList<>();
        }
        this.warnings.add(warning);
    }
    
    public void addAdditionalData(String key, Object value) {
        if (this.additionalData == null) {
            this.additionalData = new java.util.HashMap<>();
        }
        this.additionalData.put(key, value);
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
}
