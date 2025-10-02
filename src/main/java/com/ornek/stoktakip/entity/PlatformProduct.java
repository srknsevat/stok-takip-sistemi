package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_products", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "platform_id"}))
public class PlatformProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Ürün zorunludur")
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    @NotNull(message = "Platform zorunludur")
    private Platform platform;
    
    @NotBlank(message = "Platform ürün ID'si zorunludur")
    @Column(name = "platform_product_id", nullable = false)
    private String platformProductId; // Platform'daki ürün ID'si
    
    @Column(name = "platform_sku")
    private String platformSku; // Platform'daki SKU
    
    @Column(name = "platform_title")
    private String platformTitle; // Platform'daki ürün başlığı
    
    @Column(name = "platform_price", precision = 10, scale = 2)
    private BigDecimal platformPrice; // Platform'daki fiyat
    
    @Column(name = "platform_stock_quantity")
    private Integer platformStockQuantity; // Platform'daki stok miktarı
    
    @Column(name = "is_active")
    private Boolean isActive = true; // Platform'da aktif mi?
    
    @Column(name = "is_synced")
    private Boolean isSynced = false; // Son senkronizasyon başarılı mı?
    
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    @Column(name = "sync_error_message", columnDefinition = "TEXT")
    private String syncErrorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
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
    
    public PlatformProduct(Product product, Platform platform, String platformProductId) {
        this.product = product;
        this.platform = platform;
        this.platformProductId = platformProductId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    public String getPlatformProductId() {
        return platformProductId;
    }
    
    public void setPlatformProductId(String platformProductId) {
        this.platformProductId = platformProductId;
    }
    
    public String getPlatformSku() {
        return platformSku;
    }
    
    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku;
    }
    
    public String getPlatformTitle() {
        return platformTitle;
    }
    
    public void setPlatformTitle(String platformTitle) {
        this.platformTitle = platformTitle;
    }
    
    public BigDecimal getPlatformPrice() {
        return platformPrice;
    }
    
    public void setPlatformPrice(BigDecimal platformPrice) {
        this.platformPrice = platformPrice;
    }
    
    public Integer getPlatformStockQuantity() {
        return platformStockQuantity;
    }
    
    public void setPlatformStockQuantity(Integer platformStockQuantity) {
        this.platformStockQuantity = platformStockQuantity;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsSynced() {
        return isSynced;
    }
    
    public void setIsSynced(Boolean isSynced) {
        this.isSynced = isSynced;
    }
    
    public LocalDateTime getLastSyncAt() {
        return lastSyncAt;
    }
    
    public void setLastSyncAt(LocalDateTime lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }
    
    public String getSyncErrorMessage() {
        return syncErrorMessage;
    }
    
    public void setSyncErrorMessage(String syncErrorMessage) {
        this.syncErrorMessage = syncErrorMessage;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
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
}
