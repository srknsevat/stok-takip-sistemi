package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_logs")
public class SyncLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;
    
    @Column(name = "sync_type", nullable = false)
    private String syncType; // STOCK, PRODUCT, ORDER, etc.
    
    @Column(name = "sync_status", nullable = false)
    private String syncStatus; // SUCCESS, FAILED, IN_PROGRESS
    
    @Column(name = "sync_date", nullable = false)
    private LocalDateTime syncDate;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_seconds")
    private Long durationSeconds;
    
    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;
    
    @Column(name = "records_successful")
    private Integer recordsSuccessful = 0;
    
    @Column(name = "records_failed")
    private Integer recordsFailed = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "sync_details", columnDefinition = "TEXT")
    private String syncDetails; // JSON details
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (syncDate == null) {
            syncDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (startTime != null && endTime != null) {
            durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }
    
    // Constructors
    public SyncLog() {}
    
    public SyncLog(Platform platform, String syncType, String syncStatus) {
        this.platform = platform;
        this.syncType = syncType;
        this.syncStatus = syncStatus;
        this.syncDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    
    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long durationSeconds) { this.durationSeconds = durationSeconds; }
    
    public Integer getRecordsProcessed() { return recordsProcessed; }
    public void setRecordsProcessed(Integer recordsProcessed) { this.recordsProcessed = recordsProcessed; }
    
    public Integer getRecordsSuccessful() { return recordsSuccessful; }
    public void setRecordsSuccessful(Integer recordsSuccessful) { this.recordsSuccessful = recordsSuccessful; }
    
    public Integer getRecordsFailed() { return recordsFailed; }
    public void setRecordsFailed(Integer recordsFailed) { this.recordsFailed = recordsFailed; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getSyncDetails() { return syncDetails; }
    public void setSyncDetails(String syncDetails) { this.syncDetails = syncDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void startSync() {
        this.startTime = LocalDateTime.now();
        this.syncStatus = "IN_PROGRESS";
    }
    
    public void endSync(boolean success) {
        this.endTime = LocalDateTime.now();
        this.syncStatus = success ? "SUCCESS" : "FAILED";
        if (startTime != null) {
            this.durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }
    
    public void addRecordProcessed() {
        this.recordsProcessed++;
    }
    
    public void addRecordSuccessful() {
        this.recordsSuccessful++;
    }
    
    public void addRecordFailed() {
        this.recordsFailed++;
    }
}