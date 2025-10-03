package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

public class OrderProcessingResult {
    private Order order;
    private boolean success;
    private String message;
    private LocalDateTime processingDate;
    private List<String> warnings;
    private List<String> errors;
    private String status;

    public OrderProcessingResult() {
        this.processingDate = LocalDateTime.now();
    }

    public OrderProcessingResult(Order order, boolean success, String message) {
        this.order = order;
        this.success = success;
        this.message = message;
        this.processingDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getProcessingDate() { return processingDate; }
    public void setProcessingDate(LocalDateTime processingDate) { this.processingDate = processingDate; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}