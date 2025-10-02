package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.MaterialStockMovement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public interface BomExplosionService {
    
    /**
     * BOM'u patlatır ve tüm alt malzemeleri hesaplar
     */
    Map<String, BomExplosionResult> explodeBOM(Long parentMaterialId, BigDecimal quantity);
    
    /**
     * BOM'u patlatır ve stoktan düşer
     */
    boolean explodeBOMAndUpdateStock(Long parentMaterialId, BigDecimal quantity, String referenceNumber, String reason);
    
    /**
     * BOM'u patlatır ve stok kontrolü yapar (stok yeterli mi?)
     */
    BomStockCheckResult checkBOMStockAvailability(Long parentMaterialId, BigDecimal quantity);
    
    /**
     * BOM'u patlatır ve eksik stokları listeler
     */
    List<BomStockShortage> getBOMStockShortages(Long parentMaterialId, BigDecimal quantity);
    
    /**
     * BOM'u patlatır ve maliyet hesaplar
     */
    BomCostResult calculateBOMCost(Long parentMaterialId, BigDecimal quantity);
    
    /**
     * BOM'u patlatır ve MRP hesaplar
     */
    List<BomMRPItem> calculateMRP(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate);
    
    /**
     * BOM'u patlatır ve üretim planı oluşturur
     */
    BomProductionPlan createProductionPlan(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate);
    
    /**
     * BOM'u patlatır ve stok hareketleri oluşturur (commit etmeden)
     */
    List<MaterialStockMovement> createStockMovements(Long parentMaterialId, BigDecimal quantity, String referenceNumber, String reason);
    
    /**
     * BOM'u patlatır ve stok hareketlerini commit eder
     */
    boolean commitStockMovements(List<MaterialStockMovement> stockMovements);
    
    /**
     * BOM'u patlatır ve stok hareketlerini rollback eder
     */
    boolean rollbackStockMovements(List<MaterialStockMovement> stockMovements);
    
    /**
     * BOM'u patlatır ve detaylı rapor oluşturur
     */
    BomExplosionReport generateExplosionReport(Long parentMaterialId, BigDecimal quantity);
    
    // Inner Classes
    class BomExplosionResult {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private BigDecimal availableStock;
        private BigDecimal shortage;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private Integer bomLevel;
        private String bomPath;
        private List<BomExplosionResult> children;
        
        // Constructors, getters, setters
        public BomExplosionResult() {}
        
        public BomExplosionResult(MaterialCard material, BigDecimal requiredQuantity, BigDecimal availableStock) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.availableStock = availableStock;
            this.shortage = requiredQuantity.subtract(availableStock);
            this.children = new ArrayList<>();
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public BigDecimal getAvailableStock() { return availableStock; }
        public void setAvailableStock(BigDecimal availableStock) { this.availableStock = availableStock; }
        
        public BigDecimal getShortage() { return shortage; }
        public void setShortage(BigDecimal shortage) { this.shortage = shortage; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
        
        public List<BomExplosionResult> getChildren() { return children; }
        public void setChildren(List<BomExplosionResult> children) { this.children = children; }
        
        public boolean hasShortage() {
            return shortage != null && shortage.compareTo(BigDecimal.ZERO) > 0;
        }
        
        public void addChild(BomExplosionResult child) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
        }
    }
    
    class BomStockCheckResult {
        private boolean stockAvailable;
        private List<BomStockShortage> shortages;
        private BigDecimal totalShortageValue;
        private String message;
        
        public BomStockCheckResult() {
            this.shortages = new ArrayList<>();
        }
        
        public BomStockCheckResult(boolean stockAvailable, List<BomStockShortage> shortages) {
            this.stockAvailable = stockAvailable;
            this.shortages = shortages != null ? shortages : new ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isStockAvailable() { return stockAvailable; }
        public void setStockAvailable(boolean stockAvailable) { this.stockAvailable = stockAvailable; }
        
        public List<BomStockShortage> getShortages() { return shortages; }
        public void setShortages(List<BomStockShortage> shortages) { this.shortages = shortages; }
        
        public BigDecimal getTotalShortageValue() { return totalShortageValue; }
        public void setTotalShortageValue(BigDecimal totalShortageValue) { this.totalShortageValue = totalShortageValue; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    class BomStockShortage {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private BigDecimal availableStock;
        private BigDecimal shortage;
        private BigDecimal unitCost;
        private BigDecimal shortageValue;
        private Integer bomLevel;
        private String bomPath;
        
        public BomStockShortage() {}
        
        public BomStockShortage(MaterialCard material, BigDecimal requiredQuantity, BigDecimal availableStock, BigDecimal shortage) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.availableStock = availableStock;
            this.shortage = shortage;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public BigDecimal getAvailableStock() { return availableStock; }
        public void setAvailableStock(BigDecimal availableStock) { this.availableStock = availableStock; }
        
        public BigDecimal getShortage() { return shortage; }
        public void setShortage(BigDecimal shortage) { this.shortage = shortage; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public BigDecimal getShortageValue() { return shortageValue; }
        public void setShortageValue(BigDecimal shortageValue) { this.shortageValue = shortageValue; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
    }
    
    class BomCostResult {
        private BigDecimal totalCost;
        private BigDecimal totalQuantity;
        private BigDecimal unitCost;
        private List<BomExplosionResult> costBreakdown;
        private Map<String, BigDecimal> costByLevel;
        
        public BomCostResult() {
            this.costBreakdown = new ArrayList<>();
            this.costByLevel = new HashMap<>();
        }
        
        // Getters and Setters
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public BigDecimal getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(BigDecimal totalQuantity) { this.totalQuantity = totalQuantity; }
        
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        
        public List<BomExplosionResult> getCostBreakdown() { return costBreakdown; }
        public void setCostBreakdown(List<BomExplosionResult> costBreakdown) { this.costBreakdown = costBreakdown; }
        
        public Map<String, BigDecimal> getCostByLevel() { return costByLevel; }
        public void setCostByLevel(Map<String, BigDecimal> costByLevel) { this.costByLevel = costByLevel; }
    }
    
    class BomMRPItem {
        private MaterialCard material;
        private BigDecimal requiredQuantity;
        private LocalDateTime dueDate;
        private Integer bomLevel;
        private String bomPath;
        private boolean isPurchased;
        private boolean isManufactured;
        private BigDecimal leadTime;
        
        // Constructors, getters, setters
        public BomMRPItem() {}
        
        public BomMRPItem(MaterialCard material, BigDecimal requiredQuantity, LocalDateTime dueDate) {
            this.material = material;
            this.requiredQuantity = requiredQuantity;
            this.dueDate = dueDate;
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
        
        public boolean isPurchased() { return isPurchased; }
        public void setPurchased(boolean purchased) { isPurchased = purchased; }
        
        public boolean isManufactured() { return isManufactured; }
        public void setManufactured(boolean manufactured) { isManufactured = manufactured; }
        
        public BigDecimal getLeadTime() { return leadTime; }
        public void setLeadTime(BigDecimal leadTime) { this.leadTime = leadTime; }
    }
    
    class BomProductionPlan {
        private MaterialCard parentMaterial;
        private BigDecimal quantity;
        private LocalDateTime dueDate;
        private List<BomMRPItem> mrpItems;
        private BigDecimal totalCost;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        
        public BomProductionPlan() {
            this.mrpItems = new ArrayList<>();
        }
        
        // Getters and Setters
        public MaterialCard getParentMaterial() { return parentMaterial; }
        public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
        
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        
        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
        
        public List<BomMRPItem> getMrpItems() { return mrpItems; }
        public void setMrpItems(List<BomMRPItem> mrpItems) { this.mrpItems = mrpItems; }
        
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }
    
    class BomExplosionReport {
        private MaterialCard parentMaterial;
        private BigDecimal quantity;
        private LocalDateTime explosionDate;
        private List<BomExplosionResult> explosionResults;
        private BomCostResult costResult;
        private BomStockCheckResult stockCheckResult;
        private String reportSummary;
        
        public BomExplosionReport() {
            this.explosionResults = new ArrayList<>();
        }
        
        // Getters and Setters
        public MaterialCard getParentMaterial() { return parentMaterial; }
        public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
        
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        
        public LocalDateTime getExplosionDate() { return explosionDate; }
        public void setExplosionDate(LocalDateTime explosionDate) { this.explosionDate = explosionDate; }
        
        public List<BomExplosionResult> getExplosionResults() { return explosionResults; }
        public void setExplosionResults(List<BomExplosionResult> explosionResults) { this.explosionResults = explosionResults; }
        
        public BomCostResult getCostResult() { return costResult; }
        public void setCostResult(BomCostResult costResult) { this.costResult = costResult; }
        
        public BomStockCheckResult getStockCheckResult() { return stockCheckResult; }
        public void setStockCheckResult(BomStockCheckResult stockCheckResult) { this.stockCheckResult = stockCheckResult; }
        
        public String getReportSummary() { return reportSummary; }
        public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }
    }
}
