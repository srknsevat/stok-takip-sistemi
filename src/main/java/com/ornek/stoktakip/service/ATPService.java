package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ATPService {
    
    /**
     * Bir malzemenin ATP (Available to Promise) miktarını hesaplar
     */
    ATPResult calculateATP(Long materialId);
    
    /**
     * Bir malzemenin ATP miktarını BOM'a göre hesaplar
     */
    ATPResult calculateATPFromBOM(Long materialId);
    
    /**
     * Tüm platformlardaki stok miktarını ATP'ye göre günceller
     */
    boolean updateAllPlatformStocks();
    
    /**
     * Belirli bir platformdaki stok miktarını ATP'ye göre günceller
     */
    boolean updatePlatformStock(Long platformId);
    
    /**
     * Belirli bir malzemenin tüm platformlardaki stok miktarını günceller
     */
    boolean updateMaterialStockOnAllPlatforms(Long materialId);
    
    /**
     * ATP hesaplama raporu oluşturur
     */
    ATPReport generateATPReport(Long materialId);
    
    /**
     * Stok kısıtlama analizi yapar
     */
    StockConstraintAnalysis analyzeStockConstraints(Long materialId);
    
    /**
     * Stok kısıtlama önerileri verir
     */
    List<StockConstraintRecommendation> getStockConstraintRecommendations(Long materialId);
    
    /**
     * ATP hesaplama geçmişi
     */
    List<ATPCalculationHistory> getATPCalculationHistory(Long materialId);
    
    /**
     * Platform stok senkronizasyonu
     */
    PlatformStockSyncResult syncPlatformStocks(Long platformId);
    
    // Inner Classes
    class ATPResult {
        private MaterialCard material;
        private BigDecimal currentStock;
        private BigDecimal availableToPromise;
        private BigDecimal reservedQuantity;
        private BigDecimal allocatedQuantity;
        private BigDecimal safetyStock;
        private BigDecimal reorderPoint;
        private BigDecimal reorderQuantity;
        private List<ATPConstraint> constraints;
        private LocalDateTime calculationDate;
        private String calculationMethod;
        
        public ATPResult() {
            this.constraints = new ArrayList<>();
        }
        
        public ATPResult(MaterialCard material, BigDecimal currentStock, BigDecimal availableToPromise) {
            this.material = material;
            this.currentStock = currentStock;
            this.availableToPromise = availableToPromise;
            this.constraints = new ArrayList<>();
            this.calculationDate = LocalDateTime.now();
        }
        
        // Getters and Setters
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getCurrentStock() { return currentStock; }
        public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
        
        public BigDecimal getAvailableToPromise() { return availableToPromise; }
        public void setAvailableToPromise(BigDecimal availableToPromise) { this.availableToPromise = availableToPromise; }
        
        public BigDecimal getReservedQuantity() { return reservedQuantity; }
        public void setReservedQuantity(BigDecimal reservedQuantity) { this.reservedQuantity = reservedQuantity; }
        
        public BigDecimal getAllocatedQuantity() { return allocatedQuantity; }
        public void setAllocatedQuantity(BigDecimal allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
        
        public BigDecimal getSafetyStock() { return safetyStock; }
        public void setSafetyStock(BigDecimal safetyStock) { this.safetyStock = safetyStock; }
        
        public BigDecimal getReorderPoint() { return reorderPoint; }
        public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
        
        public BigDecimal getReorderQuantity() { return reorderQuantity; }
        public void setReorderQuantity(BigDecimal reorderQuantity) { this.reorderQuantity = reorderQuantity; }
        
        public List<ATPConstraint> getConstraints() { return constraints; }
        public void setConstraints(List<ATPConstraint> constraints) { this.constraints = constraints; }
        
        public LocalDateTime getCalculationDate() { return calculationDate; }
        public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
        
        public String getCalculationMethod() { return calculationMethod; }
        public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
        
        public boolean hasConstraints() {
            return constraints != null && !constraints.isEmpty();
        }
        
        public BigDecimal getEffectiveATP() {
            if (availableToPromise == null) return BigDecimal.ZERO;
            if (reservedQuantity == null) return availableToPromise;
            return availableToPromise.subtract(reservedQuantity);
        }
    }
    
    class ATPConstraint {
        private MaterialCard constraintMaterial;
        private BigDecimal requiredQuantity;
        private BigDecimal availableQuantity;
        private BigDecimal constraintQuantity;
        private Integer bomLevel;
        private String bomPath;
        private String constraintType; // STOCK, LEAD_TIME, QUALITY, etc.
        private String description;
        
        public ATPConstraint() {}
        
        public ATPConstraint(MaterialCard constraintMaterial, BigDecimal requiredQuantity, BigDecimal availableQuantity) {
            this.constraintMaterial = constraintMaterial;
            this.requiredQuantity = requiredQuantity;
            this.availableQuantity = availableQuantity;
            this.constraintQuantity = availableQuantity.divide(requiredQuantity, 0, BigDecimal.ROUND_DOWN);
        }
        
        // Getters and Setters
        public MaterialCard getConstraintMaterial() { return constraintMaterial; }
        public void setConstraintMaterial(MaterialCard constraintMaterial) { this.constraintMaterial = constraintMaterial; }
        
        public BigDecimal getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(BigDecimal requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        
        public BigDecimal getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(BigDecimal availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public BigDecimal getConstraintQuantity() { return constraintQuantity; }
        public void setConstraintQuantity(BigDecimal constraintQuantity) { this.constraintQuantity = constraintQuantity; }
        
        public Integer getBomLevel() { return bomLevel; }
        public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
        
        public String getBomPath() { return bomPath; }
        public void setBomPath(String bomPath) { this.bomPath = bomPath; }
        
        public String getConstraintType() { return constraintType; }
        public void setConstraintType(String constraintType) { this.constraintType = constraintType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isConstraint() {
            return constraintQuantity != null && constraintQuantity.compareTo(BigDecimal.ZERO) > 0;
        }
    }
    
    class ATPReport {
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
    
    class PlatformStockInfo {
        private Platform platform;
        private PlatformProduct platformProduct;
        private BigDecimal currentStock;
        private BigDecimal atpStock;
        private BigDecimal reservedStock;
        private LocalDateTime lastSync;
        private String syncStatus;
        
        public PlatformStockInfo() {}
        
        // Getters and Setters
        public Platform getPlatform() { return platform; }
        public void setPlatform(Platform platform) { this.platform = platform; }
        
        public PlatformProduct getPlatformProduct() { return platformProduct; }
        public void setPlatformProduct(PlatformProduct platformProduct) { this.platformProduct = platformProduct; }
        
        public BigDecimal getCurrentStock() { return currentStock; }
        public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
        
        public BigDecimal getAtpStock() { return atpStock; }
        public void setAtpStock(BigDecimal atpStock) { this.atpStock = atpStock; }
        
        public BigDecimal getReservedStock() { return reservedStock; }
        public void setReservedStock(BigDecimal reservedStock) { this.reservedStock = reservedStock; }
        
        public LocalDateTime getLastSync() { return lastSync; }
        public void setLastSync(LocalDateTime lastSync) { this.lastSync = lastSync; }
        
        public String getSyncStatus() { return syncStatus; }
        public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    }
    
    class StockConstraintAnalysis {
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
    
    class StockConstraintRecommendation {
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
    
    class ATPCalculationHistory {
        private Long id;
        private MaterialCard material;
        private BigDecimal atpQuantity;
        private String calculationMethod;
        private LocalDateTime calculationDate;
        private String notes;
        
        public ATPCalculationHistory() {}
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public MaterialCard getMaterial() { return material; }
        public void setMaterial(MaterialCard material) { this.material = material; }
        
        public BigDecimal getAtpQuantity() { return atpQuantity; }
        public void setAtpQuantity(BigDecimal atpQuantity) { this.atpQuantity = atpQuantity; }
        
        public String getCalculationMethod() { return calculationMethod; }
        public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
        
        public LocalDateTime getCalculationDate() { return calculationDate; }
        public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    class PlatformStockSyncResult {
        private Platform platform;
        private int totalProducts;
        private int syncedProducts;
        private int failedProducts;
        private LocalDateTime syncDate;
        private String syncStatus;
        private List<String> errors;
        
        public PlatformStockSyncResult() {
            this.errors = new ArrayList<>();
        }
        
        // Getters and Setters
        public Platform getPlatform() { return platform; }
        public void setPlatform(Platform platform) { this.platform = platform; }
        
        public int getTotalProducts() { return totalProducts; }
        public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
        
        public int getSyncedProducts() { return syncedProducts; }
        public void setSyncedProducts(int syncedProducts) { this.syncedProducts = syncedProducts; }
        
        public int getFailedProducts() { return failedProducts; }
        public void setFailedProducts(int failedProducts) { this.failedProducts = failedProducts; }
        
        public LocalDateTime getSyncDate() { return syncDate; }
        public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
        
        public String getSyncStatus() { return syncStatus; }
        public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
