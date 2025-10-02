package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_of_materials")
public class BillOfMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_material_id", nullable = false)
    @NotNull(message = "Ana malzeme zorunludur")
    private MaterialCard parentMaterial;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_material_id", nullable = false)
    @NotNull(message = "Alt malzeme zorunludur")
    private MaterialCard childMaterial;
    
    @DecimalMin(value = "0.0001", message = "Miktar sıfırdan büyük olmalıdır")
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "bom_type", nullable = false)
    private BomType bomType;
    
    @Min(value = 1, message = "BOM seviyesi 1'den küçük olamaz")
    @Column(name = "bom_level")
    private Integer bomLevel = 1;
    
    @Column(name = "operation_sequence")
    private Integer operationSequence = 0;
    
    @Column(name = "operation_name", length = 200)
    private String operationName;
    
    @Column(name = "work_center", length = 100)
    private String workCenter;
    
    @DecimalMin(value = "0.0", message = "Birim maliyet negatif olamaz")
    @Column(name = "unit_cost", precision = 15, scale = 4)
    private BigDecimal unitCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Toplam maliyet negatif olamaz")
    @Column(name = "total_cost", precision = 15, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Kurulum süresi negatif olamaz")
    @Column(name = "setup_time", precision = 10, scale = 2)
    private BigDecimal setupTime = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Çalışma süresi negatif olamaz")
    @Column(name = "run_time", precision = 10, scale = 2)
    private BigDecimal runTime = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Toplam süre negatif olamaz")
    @Column(name = "total_time", precision = 10, scale = 2)
    private BigDecimal totalTime = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Verimlilik negatif olamaz")
    @Column(name = "efficiency", precision = 5, scale = 2)
    private BigDecimal efficiency = BigDecimal.valueOf(100.0);
    
    @Column(name = "scrap_factor", precision = 5, scale = 2)
    private BigDecimal scrapFactor = BigDecimal.ZERO;
    
    @Column(name = "alternative_bom", length = 50)
    private String alternativeBom;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "is_phantom")
    private Boolean isPhantom = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
    
    @Column(name = "bom_path", length = 1000)
    private String bomPath;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Constructors
    public BillOfMaterial() {}
    
    public BillOfMaterial(MaterialCard parentMaterial, MaterialCard childMaterial, BigDecimal quantity) {
        this.parentMaterial = parentMaterial;
        this.childMaterial = childMaterial;
        this.quantity = quantity;
        this.bomType = BomType.PRODUCTION;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BillOfMaterial(MaterialCard parentMaterial, MaterialCard childMaterial, BigDecimal quantity, BomType bomType) {
        this.parentMaterial = parentMaterial;
        this.childMaterial = childMaterial;
        this.quantity = quantity;
        this.bomType = bomType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum BomType {
        PRODUCTION("Üretim"),
        ENGINEERING("Mühendislik"),
        COSTING("Maliyetlendirme"),
        PLANNING("Planlama"),
        QUALITY("Kalite");
        
        private final String displayName;
        
        BomType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalCost();
        calculateTotalTime();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalCost();
        calculateTotalTime();
    }
    
    // Helper methods
    private void calculateTotalCost() {
        if (quantity != null && unitCost != null) {
            totalCost = quantity.multiply(unitCost);
        }
    }
    
    private void calculateTotalTime() {
        if (setupTime != null && runTime != null) {
            totalTime = setupTime.add(runTime);
        }
    }
    
    public void updateCosts() {
        calculateTotalCost();
    }
    
    public void updateTimes() {
        calculateTotalTime();
    }
    
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        return (effectiveFrom == null || effectiveFrom.isBefore(now) || effectiveFrom.isEqual(now)) &&
               (effectiveTo == null || effectiveTo.isAfter(now) || effectiveTo.isEqual(now));
    }
    
    public boolean isCircularReference() {
        return parentMaterial != null && childMaterial != null && 
               parentMaterial.getId().equals(childMaterial.getId());
    }
    
    public String getBomPathString() {
        if (bomPath == null || bomPath.isEmpty()) {
            return parentMaterial.getMaterialCode() + "->" + childMaterial.getMaterialCode();
        }
        return bomPath;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getParentMaterial() { return parentMaterial; }
    public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
    
    public MaterialCard getChildMaterial() { return childMaterial; }
    public void setChildMaterial(MaterialCard childMaterial) { this.childMaterial = childMaterial; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { 
        this.quantity = quantity;
        calculateTotalCost();
    }
    
    public BomType getBomType() { return bomType; }
    public void setBomType(BomType bomType) { this.bomType = bomType; }
    
    public Integer getBomLevel() { return bomLevel; }
    public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
    
    public Integer getOperationSequence() { return operationSequence; }
    public void setOperationSequence(Integer operationSequence) { this.operationSequence = operationSequence; }
    
    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }
    
    public String getWorkCenter() { return workCenter; }
    public void setWorkCenter(String workCenter) { this.workCenter = workCenter; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { 
        this.unitCost = unitCost;
        calculateTotalCost();
    }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public BigDecimal getSetupTime() { return setupTime; }
    public void setSetupTime(BigDecimal setupTime) { 
        this.setupTime = setupTime;
        calculateTotalTime();
    }
    
    public BigDecimal getRunTime() { return runTime; }
    public void setRunTime(BigDecimal runTime) { 
        this.runTime = runTime;
        calculateTotalTime();
    }
    
    public BigDecimal getTotalTime() { return totalTime; }
    public void setTotalTime(BigDecimal totalTime) { this.totalTime = totalTime; }
    
    public BigDecimal getEfficiency() { return efficiency; }
    public void setEfficiency(BigDecimal efficiency) { this.efficiency = efficiency; }
    
    public BigDecimal getScrapFactor() { return scrapFactor; }
    public void setScrapFactor(BigDecimal scrapFactor) { this.scrapFactor = scrapFactor; }
    
    public String getAlternativeBom() { return alternativeBom; }
    public void setAlternativeBom(String alternativeBom) { this.alternativeBom = alternativeBom; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Boolean getIsPhantom() { return isPhantom; }
    public void setIsPhantom(Boolean isPhantom) { this.isPhantom = isPhantom; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    
    public String getBomPath() { return bomPath; }
    public void setBomPath(String bomPath) { this.bomPath = bomPath; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    @Override
    public String toString() {
        return "BillOfMaterial{" +
                "id=" + id +
                ", parentMaterial=" + (parentMaterial != null ? parentMaterial.getMaterialCode() : "null") +
                ", childMaterial=" + (childMaterial != null ? childMaterial.getMaterialCode() : "null") +
                ", quantity=" + quantity +
                ", bomType=" + bomType +
                ", bomLevel=" + bomLevel +
                '}';
    }
}