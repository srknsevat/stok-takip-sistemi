package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    
    List<SyncLog> findByPlatformId(Long platformId);
    
    List<SyncLog> findByStatus(String status);
    
    List<SyncLog> findByPlatformIdAndStatus(Long platformId, String status);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncDate >= :startDate AND sl.syncDate <= :endDate")
    List<SyncLog> findBySyncDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platformId = :platformId AND sl.syncDate >= :startDate AND sl.syncDate <= :endDate")
    List<SyncLog> findByPlatformIdAndSyncDateBetween(@Param("platformId") Long platformId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sl FROM SyncLog sl ORDER BY sl.syncDate DESC")
    List<SyncLog> findRecentSyncLogs();
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platformId = :platformId ORDER BY sl.syncDate DESC")
    List<SyncLog> findRecentSyncLogsByPlatform(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.platformId = :platformId AND sl.status = :status")
    long countByPlatformIdAndStatus(@Param("platformId") Long platformId, @Param("status") String status);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.status = 'FAILED' ORDER BY sl.syncDate DESC")
    List<SyncLog> findFailedSyncs();
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platformId = :platformId AND sl.status = 'FAILED' ORDER BY sl.syncDate DESC")
    List<SyncLog> findFailedSyncsByPlatform(@Param("platformId") Long platformId);
}