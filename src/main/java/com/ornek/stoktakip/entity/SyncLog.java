package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_logs")
public class SyncLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    @NotNull(message = "Platform zorunludur")
    private Platform platform;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false)
    private SyncType syncType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false)
    private SyncStatus syncStatus;
    
    @Column(name = "sync_message", columnDefinition = "TEXT")
    private String syncMessage;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData; // JSON formatında gönderilen veri
    
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // JSON formatında dönen veri
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs; // İşlem süresi (milisaniye)
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retry_count")
    private Integer maxRetryCount = 3;
    
    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum SyncType {
        PRODUCT_CREATE("Ürün Oluşturma"),
        PRODUCT_UPDATE("Ürün Güncelleme"),
        PRODUCT_DELETE("Ürün Silme"),
        STOCK_UPDATE("Stok Güncelleme"),
        ORDER_CREATE("Sipariş Oluşturma"),
        ORDER_UPDATE("Sipariş Güncelleme"),
        ORDER_CANCEL("Sipariş İptal"),
        ORDER_RETURN("Sipariş İade"),
        FULL_SYNC("Tam Senkronizasyon"),
        WEBHOOK_PROCESS("Webhook İşleme");
        
        private final String displayName;
        
        SyncType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum SyncStatus {
        PENDING("Beklemede"),
        IN_PROGRESS("İşleniyor"),
        SUCCESS("Başarılı"),
        FAILED("Başarısız"),
        RETRY("Tekrar Denenecek"),
        CANCELLED("İptal Edildi");
        
        private final String displayName;
        
        SyncStatus(String displayName) {
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
    
    // Constructors
    public SyncLog() {}
    
    public SyncLog(Platform platform, SyncType syncType, SyncStatus syncStatus) {
        this.platform = platform;
        this.syncType = syncType;
        this.syncStatus = syncStatus;
    }
    
    // Business Methods
    public void markAsSuccess(String message) {
        this.syncStatus = SyncStatus.SUCCESS;
        this.syncMessage = message;
        this.errorMessage = null;
        this.nextRetryAt = null;
    }
    
    public void markAsFailed(String errorMessage) {
        this.syncStatus = SyncStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
        
        if (this.retryCount < this.maxRetryCount) {
            this.syncStatus = SyncStatus.RETRY;
            this.nextRetryAt = LocalDateTime.now().plusMinutes(5 * this.retryCount); // Exponential backoff
        }
    }
    
    public void markAsInProgress() {
        this.syncStatus = SyncStatus.IN_PROGRESS;
    }
    
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount && 
               (this.nextRetryAt == null || LocalDateTime.now().isAfter(this.nextRetryAt));
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public SyncType getSyncType() {
        return syncType;
    }
    
    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }
    
    public SyncStatus getSyncStatus() {
        return syncStatus;
    }
    
    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
    
    public String getSyncMessage() {
        return syncMessage;
    }
    
    public void setSyncMessage(String syncMessage) {
        this.syncMessage = syncMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getRequestData() {
        return requestData;
    }
    
    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
    
    public String getResponseData() {
        return responseData;
    }
    
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }
    
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
    
    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }
    
    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
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
