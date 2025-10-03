package com.ornek.stoktakip.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class ATPReport {
    private LocalDateTime reportDate;
    private String reportType;
    private List<ATPResult> atpResults;
    private Map<String, PlatformStockInfo> platformStockUpdates;
    private List<String> warnings;
    private List<String> errors;
    private String status;

    public ATPReport() {
        this.reportDate = LocalDateTime.now();
        this.atpResults = new ArrayList<>();
        this.platformStockUpdates = new HashMap<>();
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    // Getters and Setters
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public List<ATPResult> getAtpResults() { return atpResults; }
    public void setAtpResults(List<ATPResult> atpResults) { this.atpResults = atpResults; }
    
    public Map<String, PlatformStockInfo> getPlatformStockUpdates() { return platformStockUpdates; }
    public void setPlatformStockUpdates(Map<String, PlatformStockInfo> platformStockUpdates) { this.platformStockUpdates = platformStockUpdates; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Eksik metodlar
    public void setPlatformStockUpdates(java.util.Map<String, PlatformStockInfo> platformStockUpdates) {
        this.platformStockUpdates = platformStockUpdates;
    }
}