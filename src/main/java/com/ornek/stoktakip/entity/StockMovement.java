package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialCard material;

    @Column(nullable = false)
    private Integer quantity; // Pozitif değerler giriş, negatif değerler çıkış

    @Column(nullable = false)
    private LocalDateTime movementDate;

    @Column(nullable = false)
    private String movementType; // "ENTRY" veya "EXIT"
    
    @Column(columnDefinition = "TEXT")
    private String description;

    // Getter ve Setter metodları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaterialCard getMaterial() {
        return material;
    }

    public void setMaterial(MaterialCard material) {
        this.material = material;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Eksik metod - geriye uyumluluk için
    public void setProduct(Product product) {
        // Product'ı MaterialCard'a dönüştürme gerekebilir
        // Şimdilik placeholder
    }
} 