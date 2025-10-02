package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_logs")
public class SyncLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "platform_id", nullable = false)
    private Long platformId;
    
    @Column(name = "sync_type", nullable = false)
    private String syncType; // FULL, INCREMENTAL, STOCK_UPDATE
    
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED, IN_PROGRESS
    
    @Column(name = "sync_date", nullable = false)
    private LocalDateTime syncDate;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_seconds")
    private Long durationSeconds;
    
    @Column(name = "records_processed")
    private Integer recordsProcessed;
    
    @Column(name = "records_success")
    private Integer recordsSuccess;
    
    @Column(name = "records_failed")
    private Integer recordsFailed;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "sync_details", columnDefinition = "TEXT")
    private String syncDetails;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (syncDate == null) {
            syncDate = LocalDateTime.now();
        }
    }
    
    // Constructors
    public SyncLog() {}
    
    public SyncLog(Long platformId, String syncType, String status) {
        this.platformId = platformId;
        this.syncType = syncType;
        this.status = status;
        this.syncDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }
    
    public String getSyncType() { return syncType; }
    public void setSyncType(String syncType) { this.syncType = syncType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    
    public Integer getRecordsSuccess() { return recordsSuccess; }
    public void setRecordsSuccess(Integer recordsSuccess) { this.recordsSuccess = recordsSuccess; }
    
    public Integer getRecordsFailed() { return recordsFailed; }
    public void setRecordsFailed(Integer recordsFailed) { this.recordsFailed = recordsFailed; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getSyncDetails() { return syncDetails; }
    public void setSyncDetails(String syncDetails) { this.syncDetails = syncDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}