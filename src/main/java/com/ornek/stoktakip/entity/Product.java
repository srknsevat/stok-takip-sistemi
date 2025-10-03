package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validation.product.name.notblank}")
    @Size(min = 3, max = 100, message = "{validation.product.name.size}")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "{validation.product.price.notnull}")
    @DecimalMin(value = "0.0", message = "{validation.product.price.min}")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "{validation.product.stock.notnull}")
    @Min(value = 0, message = "{validation.product.stock.min}")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @NotNull(message = "{validation.product.date.notnull}")
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column
    private String categories;

    @NotBlank(message = "{validation.product.code.notblank}")
    @Size(min = 2, max = 20, message = "{validation.product.code.size}")
    @Column(nullable = false, unique = true)
    private String code;

    // E-ticaret entegrasyonu için yeni alanlar
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "sync_enabled")
    private Boolean syncEnabled = true;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // İlişkiler
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlatformProduct> platformProducts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

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

    // Kategorileri Liste olarak almak için yardımcı metod
    @Transient // Bu alanın veritabanında saklanmamasını sağlar
    public List<String> getCategoryList() {
        return categories != null ? Arrays.asList(categories.split(",")) : List.of();
    }

    // Kategori listesini String'e çevirmek için yardımcı metod
    public void setCategoryList(List<String> categoryList) {
        this.categories = String.join(",", categoryList);
    }

    // Getter ve Setter metodları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // E-ticaret entegrasyonu için yeni getter/setter'lar
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(Boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PlatformProduct> getPlatformProducts() {
        return platformProducts;
    }

    public void setPlatformProducts(List<PlatformProduct> platformProducts) {
        this.platformProducts = platformProducts;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    // Eksik metodlar
    public BigDecimal getCurrentStock() { 
        return stock != null ? stock : BigDecimal.ZERO; 
    }
    
    public void setCurrentStock(BigDecimal currentStock) { 
        this.stock = currentStock; 
    }
    
    public String getMaterialCode() { 
        return code; 
    }
    
    public void setMaterialCode(String materialCode) { 
        this.code = materialCode; 
    }
} 