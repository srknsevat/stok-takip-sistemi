package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderProcessingReport {
    private Order order;
    private LocalDateTime reportDate;
    private String reportType;
    private OrderStockCheckResult stockCheck;
    private OrderCostResult costAnalysis;
    private List<String> recommendations;
    private Map<String, Object> processingMetrics;
    private String status;

    public OrderProcessingReport() {
        this.reportDate = LocalDateTime.now();
    }

    public OrderProcessingReport(Order order, String reportType) {
        this.order = order;
        this.reportType = reportType;
        this.reportDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public OrderStockCheckResult getStockCheck() { return stockCheck; }
    public void setStockCheck(OrderStockCheckResult stockCheck) { this.stockCheck = stockCheck; }
    
    public OrderCostResult getCostAnalysis() { return costAnalysis; }
    public void setCostAnalysis(OrderCostResult costAnalysis) { this.costAnalysis = costAnalysis; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public Map<String, Object> getProcessingMetrics() { return processingMetrics; }
    public void setProcessingMetrics(Map<String, Object> processingMetrics) { this.processingMetrics = processingMetrics; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
