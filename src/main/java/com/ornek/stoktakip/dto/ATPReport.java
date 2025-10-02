package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ATPReport {
    private MaterialCard material;
    private LocalDateTime reportDate;
    private ATPResult atpResult;
    private List<ATPConstraint> constraints;
    private List<PlatformStockInfo> platformStocks;
    private String summary;
    private Map<String, BigDecimal> stockByLevel;
    
    public ATPReport() {
        this.constraints = new ArrayList<>();
        this.platformStocks = new ArrayList<>();
        this.stockByLevel = new HashMap<>();
    }
    
    // Getters and Setters
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    
    public ATPResult getAtpResult() { return atpResult; }
    public void setAtpResult(ATPResult atpResult) { this.atpResult = atpResult; }
    
    public List<ATPConstraint> getConstraints() { return constraints; }
    public void setConstraints(List<ATPConstraint> constraints) { this.constraints = constraints; }
    
    public List<PlatformStockInfo> getPlatformStocks() { return platformStocks; }
    public void setPlatformStocks(List<PlatformStockInfo> platformStocks) { this.platformStocks = platformStocks; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public Map<String, BigDecimal> getStockByLevel() { return stockByLevel; }
    public void setStockByLevel(Map<String, BigDecimal> stockByLevel) { this.stockByLevel = stockByLevel; }
}
