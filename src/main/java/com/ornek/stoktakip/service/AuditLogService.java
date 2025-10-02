package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.AuditLog;
import com.ornek.stoktakip.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    
    void logAction(User user, String action, String entityType, Long entityId);
    
    void logAction(User user, String action, String entityType, Long entityId, String oldValues, String newValues);
    
    void logAction(User user, String action, String entityType, Long entityId, String oldValues, String newValues, String ipAddress, String userAgent);
    
    List<AuditLog> getAuditLogsByUser(Long userId);
    
    List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId);
    
    List<AuditLog> getAuditLogsByAction(String action);
    
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<AuditLog> getRecentAuditLogs(int limit);
    
    long getAuditLogCount();
    
    long getAuditLogCountByUser(Long userId);
    
    long getAuditLogCountByEntity(String entityType);
    
    long getAuditLogCountByAction(String action);
}
