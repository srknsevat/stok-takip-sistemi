package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.AuditLog;
import com.ornek.stoktakip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserId(Long userId);
    
    List<AuditLog> findByEntityType(String entityType);
    
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<AuditLog> findByEntityTypeAndCreatedAtBetween(String entityType, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT al FROM AuditLog al WHERE al.user = :user ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType ORDER BY al.createdAt DESC")
    List<AuditLog> findByEntityTypeOrderByCreatedAtDesc(@Param("entityType") String entityType);
    
    @Query("SELECT al FROM AuditLog al WHERE al.action = :action ORDER BY al.createdAt DESC")
    List<AuditLog> findByActionOrderByCreatedAtDesc(@Param("action") String action);
    
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.entityType = :entityType")
    Long countByEntityType(@Param("entityType") String entityType);
    
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.action = :action")
    Long countByAction(@Param("action") String action);
    
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt >= :startDate ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentLogs(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT al FROM AuditLog al WHERE al.ipAddress = :ipAddress ORDER BY al.createdAt DESC")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);
}
