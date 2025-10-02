package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_stock_movements")
public class MaterialStockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    @NotNull(message = "Malzeme zorunludur")
    private MaterialCard material;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;
    
    @DecimalMin(value = "0.0001", message = "Miktar sıfırdan büyük olmalıdır")
    @Column(name = "quantity", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;
    
    @DecimalMin(value = "0.0", message = "Birim maliyet negatif olamaz")
    @Column(name = "unit_cost", precision = 15, scale = 4)
    private BigDecimal unitCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Toplam maliyet negatif olamaz")
    @Column(name = "total_cost", precision = 15, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;
    
    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;
    
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;
    
    @Column(name = "batch_number", length = 100)
    private String batchNumber;
    
    @Column(name = "serial_number", length = 100)
    private String serialNumber;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "reason", length = 500)
    private String reason;
    
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
    public MaterialStockMovement() {}
    
    public MaterialStockMovement(MaterialCard material, MovementType movementType, BigDecimal quantity) {
        this.material = material;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MaterialStockMovement(MaterialCard material, MovementType movementType, BigDecimal quantity, 
                               BigDecimal unitCost, String reason) {
        this.material = material;
        this.movementType = movementType;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.reason = reason;
        this.movementDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateTotalCost();
    }
    
    // Enums
    public enum MovementType {
        IN("Giriş"),
        OUT("Çıkış"),
        PRODUCTION_IN("Üretim Girişi"),
        PRODUCTION_OUT("Üretim Çıkışı"),
        PURCHASE("Satın Alma"),
        SALES("Satış"),
        TRANSFER_IN("Transfer Girişi"),
        TRANSFER_OUT("Transfer Çıkışı"),
        ADJUSTMENT("Düzeltme"),
        RETURN("İade"),
        SCRAP("Hurda"),
        CYCLE_COUNT("Sayım"),
        PHYSICAL_INVENTORY("Fiziki Envanter");
        
        private final String displayName;
        
        MovementType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public boolean isInbound() {
            return this == IN || this == PRODUCTION_IN || this == PURCHASE || 
                   this == TRANSFER_IN || this == RETURN || this == ADJUSTMENT;
        }
        
        public boolean isOutbound() {
            return this == OUT || this == PRODUCTION_OUT || this == SALES || 
                   this == TRANSFER_OUT || this == SCRAP || this == ADJUSTMENT;
        }
    }
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
        calculateTotalCost();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalCost();
    }
    
    // Helper methods
    private void calculateTotalCost() {
        if (quantity != null && unitCost != null) {
            totalCost = quantity.multiply(unitCost);
        }
    }
    
    public void updateCosts() {
        calculateTotalCost();
    }
    
    public boolean isInbound() {
        return movementType != null && movementType.isInbound();
    }
    
    public boolean isOutbound() {
        return movementType != null && movementType.isOutbound();
    }
    
    public BigDecimal getEffectiveQuantity() {
        if (isInbound()) {
            return quantity;
        } else if (isOutbound()) {
            return quantity.negate();
        }
        return BigDecimal.ZERO;
    }
    
    public boolean hasBatch() {
        return batchNumber != null && !batchNumber.trim().isEmpty();
    }
    
    public boolean hasSerial() {
        return serialNumber != null && !serialNumber.trim().isEmpty();
    }
    
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }
    
    public boolean isExpiringSoon(int days) {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now().plusDays(days));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public MaterialCard getMaterial() { return material; }
    public void setMaterial(MaterialCard material) { this.material = material; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { 
        this.quantity = quantity;
        calculateTotalCost();
    }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { 
        this.unitCost = unitCost;
        calculateTotalCost();
    }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
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
        return "MaterialStockMovement{" +
                "id=" + id +
                ", material=" + (material != null ? material.getMaterialCode() : "null") +
                ", movementType=" + movementType +
                ", quantity=" + quantity +
                ", movementDate=" + movementDate +
                '}';
    }
}