package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.AuditLog;
import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.repository.AuditLogRepository;
import com.ornek.stoktakip.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    @Override
    public void logAction(User user, String action, String entityType, Long entityId) {
        logAction(user, action, entityType, entityId, null, null, null, null);
    }
    
    @Override
    public void logAction(User user, String action, String entityType, Long entityId, String oldValues, String newValues) {
        logAction(user, action, entityType, entityId, oldValues, newValues, null, null);
    }
    
    @Override
    public void logAction(User user, String action, String entityType, Long entityId, String oldValues, String newValues, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs(int limit) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<AuditLog> logs = auditLogRepository.findRecentLogs(startDate);
        return logs.size() > limit ? logs.subList(0, limit) : logs;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCount() {
        return auditLogRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCountByUser(Long userId) {
        return auditLogRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCountByEntity(String entityType) {
        return auditLogRepository.countByEntityType(entityType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getAuditLogCountByAction(String action) {
        return auditLogRepository.countByAction(action);
    }
}
