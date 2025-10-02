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
    
    List<SyncLog> findBySyncStatus(String syncStatus);
    
    List<SyncLog> findBySyncType(String syncType);
    
    List<SyncLog> findBySyncDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<SyncLog> findByPlatformIdAndSyncDateBetween(Long platformId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<SyncLog> findByPlatformIdAndSyncStatus(Long platformId, String syncStatus);
    
    List<SyncLog> findBySyncStatusAndSyncDateBetween(String syncStatus, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platform.id = :platformId ORDER BY sl.syncDate DESC")
    List<SyncLog> findRecentByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus = 'FAILED' ORDER BY sl.syncDate DESC")
    List<SyncLog> findFailedSyncs();
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus = 'SUCCESS' ORDER BY sl.syncDate DESC")
    List<SyncLog> findSuccessfulSyncs();
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platform.id = :platformId AND sl.syncStatus = 'FAILED' ORDER BY sl.syncDate DESC")
    List<SyncLog> findFailedSyncsByPlatform(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.platform.id = :platformId")
    Long countByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.syncStatus = :status")
    Long countBySyncStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.syncType = :type")
    Long countBySyncType(@Param("type") String type);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.platform.id = :platformId AND sl.syncStatus = :status")
    Long countByPlatformIdAndSyncStatus(@Param("platformId") Long platformId, @Param("status") String status);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncDate >= :startDate AND sl.syncDate <= :endDate ORDER BY sl.syncDate DESC")
    List<SyncLog> findRecentSyncs(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}