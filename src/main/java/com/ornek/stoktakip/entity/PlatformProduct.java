package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_products")
public class PlatformProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private MaterialCard product;
    
    @Column(name = "platform_product_id")
    private String platformProductId;
    
    @Column(name = "platform_sku")
    private String platformSku;
    
    @Column(name = "platform_title")
    private String platformTitle;
    
    @Column(name = "platform_description", columnDefinition = "TEXT")
    private String platformDescription;
    
    @Column(name = "platform_price")
    private BigDecimal platformPrice;
    
    @Column(name = "platform_stock_quantity")
    private Integer platformStockQuantity;
    
    @Column(name = "platform_currency", length = 3)
    private String platformCurrency = "TRY";
    
    @Column(name = "platform_category")
    private String platformCategory;
    
    @Column(name = "platform_brand")
    private String platformBrand;
    
    @Column(name = "platform_condition")
    private String platformCondition = "NEW";
    
    @Column(name = "platform_weight")
    private BigDecimal platformWeight;
    
    @Column(name = "platform_dimensions")
    private String platformDimensions;
    
    @Column(name = "platform_images", columnDefinition = "TEXT")
    private String platformImages;
    
    @Column(name = "platform_tags", columnDefinition = "TEXT")
    private String platformTags;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_synced")
    private Boolean isSynced = false;
    
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    @Column(name = "sync_error", columnDefinition = "TEXT")
    private String syncError;
    
    @Column(name = "sync_attempts")
    private Integer syncAttempts = 0;
    
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
    public PlatformProduct() {}
    
    public PlatformProduct(Platform platform, MaterialCard product, String platformProductId, String platformSku) {
        this.platform = platform;
        this.product = product;
        this.platformProductId = platformProductId;
        this.platformSku = platformSku;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public MaterialCard getProduct() { return product; }
    public void setProduct(MaterialCard product) { this.product = product; }
    
    public String getPlatformProductId() { return platformProductId; }
    public void setPlatformProductId(String platformProductId) { this.platformProductId = platformProductId; }
    
    public String getPlatformSku() { return platformSku; }
    public void setPlatformSku(String platformSku) { this.platformSku = platformSku; }
    
    public String getPlatformTitle() { return platformTitle; }
    public void setPlatformTitle(String platformTitle) { this.platformTitle = platformTitle; }
    
    public String getPlatformDescription() { return platformDescription; }
    public void setPlatformDescription(String platformDescription) { this.platformDescription = platformDescription; }
    
    public BigDecimal getPlatformPrice() { return platformPrice; }
    public void setPlatformPrice(BigDecimal platformPrice) { this.platformPrice = platformPrice; }
    
    public Integer getPlatformStockQuantity() { return platformStockQuantity; }
    public void setPlatformStockQuantity(Integer platformStockQuantity) { this.platformStockQuantity = platformStockQuantity; }
    
    public String getPlatformCurrency() { return platformCurrency; }
    public void setPlatformCurrency(String platformCurrency) { this.platformCurrency = platformCurrency; }
    
    public String getPlatformCategory() { return platformCategory; }
    public void setPlatformCategory(String platformCategory) { this.platformCategory = platformCategory; }
    
    public String getPlatformBrand() { return platformBrand; }
    public void setPlatformBrand(String platformBrand) { this.platformBrand = platformBrand; }
    
    public String getPlatformCondition() { return platformCondition; }
    public void setPlatformCondition(String platformCondition) { this.platformCondition = platformCondition; }
    
    public BigDecimal getPlatformWeight() { return platformWeight; }
    public void setPlatformWeight(BigDecimal platformWeight) { this.platformWeight = platformWeight; }
    
    public String getPlatformDimensions() { return platformDimensions; }
    public void setPlatformDimensions(String platformDimensions) { this.platformDimensions = platformDimensions; }
    
    public String getPlatformImages() { return platformImages; }
    public void setPlatformImages(String platformImages) { this.platformImages = platformImages; }
    
    public String getPlatformTags() { return platformTags; }
    public void setPlatformTags(String platformTags) { this.platformTags = platformTags; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsSynced() { return isSynced; }
    public void setIsSynced(Boolean isSynced) { this.isSynced = isSynced; }
    
    public LocalDateTime getLastSyncAt() { return lastSyncAt; }
    public void setLastSyncAt(LocalDateTime lastSyncAt) { this.lastSyncAt = lastSyncAt; }
    
    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }
    
    public Integer getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(Integer syncAttempts) { this.syncAttempts = syncAttempts; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}