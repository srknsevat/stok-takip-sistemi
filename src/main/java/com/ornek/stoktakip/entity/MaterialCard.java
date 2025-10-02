package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "material_cards")
public class MaterialCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Malzeme kodu zorunludur")
    @Column(name = "material_code", nullable = false, unique = true, length = 50)
    private String materialCode;
    
    @NotBlank(message = "Malzeme adı zorunludur")
    @Column(name = "material_name", nullable = false, length = 200)
    private String materialName;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false)
    private MaterialType materialType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "material_category", nullable = false)
    private MaterialCategory materialCategory;
    
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;
    
    @DecimalMin(value = "0.0", message = "Mevcut stok negatif olamaz")
    @Column(name = "current_stock", precision = 15, scale = 3)
    private BigDecimal currentStock = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Minimum stok negatif olamaz")
    @Column(name = "min_stock_level", precision = 15, scale = 3)
    private BigDecimal minStockLevel = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Maksimum stok negatif olamaz")
    @Column(name = "max_stock_level", precision = 15, scale = 3)
    private BigDecimal maxStockLevel = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Sipariş noktası negatif olamaz")
    @Column(name = "reorder_point", precision = 15, scale = 3)
    private BigDecimal reorderPoint = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Sipariş miktarı negatif olamaz")
    @Column(name = "reorder_quantity", precision = 15, scale = 3)
    private BigDecimal reorderQuantity = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Standart maliyet negatif olamaz")
    @Column(name = "standard_cost", precision = 15, scale = 4)
    private BigDecimal standardCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Ortalama maliyet negatif olamaz")
    @Column(name = "average_cost", precision = 15, scale = 4)
    private BigDecimal averageCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Son alış maliyeti negatif olamaz")
    @Column(name = "last_purchase_cost", precision = 15, scale = 4)
    private BigDecimal lastPurchaseCost = BigDecimal.ZERO;
    
    @Column(name = "supplier_code", length = 50)
    private String supplierCode;
    
    @Column(name = "supplier_name", length = 200)
    private String supplierName;
    
    @Column(name = "supplier_contact", length = 200)
    private String supplierContact;
    
    @Column(name = "lead_time_days")
    private Integer leadTimeDays;
    
    @Column(name = "storage_location", length = 100)
    private String storageLocation;
    
    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;
    
    @Column(name = "hazardous_material")
    private Boolean hazardousMaterial = false;
    
    @Column(name = "batch_controlled")
    private Boolean batchControlled = false;
    
    @Column(name = "serial_controlled")
    private Boolean serialControlled = false;
    
    @Column(name = "quality_grade", length = 50)
    private String qualityGrade;
    
    @Column(name = "certification_required")
    private Boolean certificationRequired = false;
    
    @Column(name = "inspection_required")
    private Boolean inspectionRequired = false;
    
    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;
    
    @Column(name = "dimensions", length = 100)
    private String dimensions;
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "brand", length = 100)
    private String brand;
    
    @Column(name = "model", length = 100)
    private String model;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_obsolete")
    private Boolean isObsolete = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // İlişkiler
    @OneToMany(mappedBy = "parentMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillOfMaterial> parentBOMs;
    
    @OneToMany(mappedBy = "childMaterial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillOfMaterial> childBOMs;
    
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaterialStockMovement> stockMovements;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlatformProduct> platformProducts;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    // Constructors
    public MaterialCard() {}
    
    public MaterialCard(String materialCode, String materialName, MaterialType materialType, MaterialCategory materialCategory) {
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialType = materialType;
        this.materialCategory = materialCategory;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum MaterialType {
        RAW_MATERIAL("Ham Malzeme"),
        SEMI_FINISHED("Yarı Mamul"),
        FINISHED_GOOD("Mamul"),
        CONSUMABLE("Sarf Malzeme"),
        TOOL("Takım"),
        EQUIPMENT("Ekipman"),
        SERVICE("Hizmet");
        
        private final String displayName;
        
        MaterialType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum MaterialCategory {
        ELECTRONICS("Elektronik"),
        MECHANICAL("Mekanik"),
        CHEMICAL("Kimyasal"),
        TEXTILE("Tekstil"),
        FOOD("Gıda"),
        CONSTRUCTION("İnşaat"),
        AUTOMOTIVE("Otomotiv"),
        MEDICAL("Medikal"),
        OTHER("Diğer");
        
        private final String displayName;
        
        MaterialCategory(String displayName) {
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
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMaterialCode() { return materialCode; }
    public void setMaterialCode(String materialCode) { this.materialCode = materialCode; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public MaterialType getMaterialType() { return materialType; }
    public void setMaterialType(MaterialType materialType) { this.materialType = materialType; }
    
    public MaterialCategory getMaterialCategory() { return materialCategory; }
    public void setMaterialCategory(MaterialCategory materialCategory) { this.materialCategory = materialCategory; }
    
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    
    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
    
    public BigDecimal getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(BigDecimal minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public BigDecimal getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(BigDecimal maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    
    public BigDecimal getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(BigDecimal reorderQuantity) { this.reorderQuantity = reorderQuantity; }
    
    public BigDecimal getStandardCost() { return standardCost; }
    public void setStandardCost(BigDecimal standardCost) { this.standardCost = standardCost; }
    
    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    
    public BigDecimal getLastPurchaseCost() { return lastPurchaseCost; }
    public void setLastPurchaseCost(BigDecimal lastPurchaseCost) { this.lastPurchaseCost = lastPurchaseCost; }
    
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getSupplierContact() { return supplierContact; }
    public void setSupplierContact(String supplierContact) { this.supplierContact = supplierContact; }
    
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    
    public String getStorageConditions() { return storageConditions; }
    public void setStorageConditions(String storageConditions) { this.storageConditions = storageConditions; }
    
    public Boolean getHazardousMaterial() { return hazardousMaterial; }
    public void setHazardousMaterial(Boolean hazardousMaterial) { this.hazardousMaterial = hazardousMaterial; }
    
    public Boolean getBatchControlled() { return batchControlled; }
    public void setBatchControlled(Boolean batchControlled) { this.batchControlled = batchControlled; }
    
    public Boolean getSerialControlled() { return serialControlled; }
    public void setSerialControlled(Boolean serialControlled) { this.serialControlled = serialControlled; }
    
    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }
    
    public Boolean getCertificationRequired() { return certificationRequired; }
    public void setCertificationRequired(Boolean certificationRequired) { this.certificationRequired = certificationRequired; }
    
    public Boolean getInspectionRequired() { return inspectionRequired; }
    public void setInspectionRequired(Boolean inspectionRequired) { this.inspectionRequired = inspectionRequired; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsObsolete() { return isObsolete; }
    public void setIsObsolete(Boolean isObsolete) { this.isObsolete = isObsolete; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    public List<BillOfMaterial> getParentBOMs() { return parentBOMs; }
    public void setParentBOMs(List<BillOfMaterial> parentBOMs) { this.parentBOMs = parentBOMs; }
    
    public List<BillOfMaterial> getChildBOMs() { return childBOMs; }
    public void setChildBOMs(List<BillOfMaterial> childBOMs) { this.childBOMs = childBOMs; }
    
    public List<MaterialStockMovement> getStockMovements() { return stockMovements; }
    public void setStockMovements(List<MaterialStockMovement> stockMovements) { this.stockMovements = stockMovements; }
    
    public List<PlatformProduct> getPlatformProducts() { return platformProducts; }
    public void setPlatformProducts(List<PlatformProduct> platformProducts) { this.platformProducts = platformProducts; }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    
    // Helper methods
    public boolean isLowStock() {
        return currentStock.compareTo(minStockLevel) <= 0;
    }
    
    public boolean isHighStock() {
        return maxStockLevel.compareTo(BigDecimal.ZERO) > 0 && currentStock.compareTo(maxStockLevel) >= 0;
    }
    
    public boolean needsReorder() {
        return currentStock.compareTo(reorderPoint) <= 0;
    }
    
    public BigDecimal getStockValue() {
        return currentStock.multiply(averageCost);
    }
    
    public String getFullName() {
        return materialCode + " - " + materialName;
    }
    
    @Override
    public String toString() {
        return "MaterialCard{" +
                "id=" + id +
                ", materialCode='" + materialCode + '\'' +
                ", materialName='" + materialName + '\'' +
                ", materialType=" + materialType +
                ", currentStock=" + currentStock +
                '}';
    }
}