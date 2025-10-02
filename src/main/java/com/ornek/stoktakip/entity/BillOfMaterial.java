package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
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
    private MaterialCard parentMaterial;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_material_id", nullable = false)
    private MaterialCard componentMaterial;
    
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "unit", length = 20)
    private String unit = "ADET";
    
    @Column(name = "bom_level")
    private Integer bomLevel = 1;
    
    @Column(name = "bom_type", length = 50)
    private String bomType = "STANDARD";
    
    @Column(name = "efficiency", precision = 5, scale = 2)
    private BigDecimal efficiency = BigDecimal.valueOf(100.00);
    
    @Column(name = "operation_sequence")
    private Integer operationSequence = 1;
    
    @Column(name = "operation_description", columnDefinition = "TEXT")
    private String operationDescription;
    
    @Column(name = "setup_time")
    private Integer setupTime = 0;
    
    @Column(name = "run_time")
    private Integer runTime = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_phantom")
    private Boolean isPhantom = false;
    
    @Column(name = "is_purchased")
    private Boolean isPurchased = false;
    
    @Column(name = "lead_time")
    private Integer leadTime = 0;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public BillOfMaterial() {}
    
    public BillOfMaterial(MaterialCard parentMaterial, MaterialCard componentMaterial, BigDecimal quantity) {
        this.parentMaterial = parentMaterial;
        this.componentMaterial = componentMaterial;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getParentMaterial() { return parentMaterial; }
    public void setParentMaterial(MaterialCard parentMaterial) { this.parentMaterial = parentMaterial; }
    
    public MaterialCard getComponentMaterial() { return componentMaterial; }
    public void setComponentMaterial(MaterialCard componentMaterial) { this.componentMaterial = componentMaterial; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public Integer getBomLevel() { return bomLevel; }
    public void setBomLevel(Integer bomLevel) { this.bomLevel = bomLevel; }
    
    public String getBomType() { return bomType; }
    public void setBomType(String bomType) { this.bomType = bomType; }
    
    public BigDecimal getEfficiency() { return efficiency; }
    public void setEfficiency(BigDecimal efficiency) { this.efficiency = efficiency; }
    
    public Integer getOperationSequence() { return operationSequence; }
    public void setOperationSequence(Integer operationSequence) { this.operationSequence = operationSequence; }
    
    public String getOperationDescription() { return operationDescription; }
    public void setOperationDescription(String operationDescription) { this.operationDescription = operationDescription; }
    
    public Integer getSetupTime() { return setupTime; }
    public void setSetupTime(Integer setupTime) { this.setupTime = setupTime; }
    
    public Integer getRunTime() { return runTime; }
    public void setRunTime(Integer runTime) { this.runTime = runTime; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsPhantom() { return isPhantom; }
    public void setIsPhantom(Boolean isPhantom) { this.isPhantom = isPhantom; }
    
    public Boolean getIsPurchased() { return isPurchased; }
    public void setIsPurchased(Boolean isPurchased) { this.isPurchased = isPurchased; }
    
    public Integer getLeadTime() { return leadTime; }
    public void setLeadTime(Integer leadTime) { this.leadTime = leadTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}