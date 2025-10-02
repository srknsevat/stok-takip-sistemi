package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.Product;
import com.ornek.stoktakip.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {
    
    List<SyncLog> findByPlatform(Platform platform);
    
    List<SyncLog> findByProduct(Product product);
    
    List<SyncLog> findBySyncStatus(SyncLog.SyncStatus syncStatus);
    
    List<SyncLog> findBySyncType(SyncLog.SyncType syncType);
    
    List<SyncLog> findByPlatformAndSyncStatus(Platform platform, SyncLog.SyncStatus syncStatus);
    
    List<SyncLog> findByProductAndSyncStatus(Product product, SyncLog.SyncStatus syncStatus);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus = 'RETRY' AND sl.nextRetryAt <= :now")
    List<SyncLog> findRetryableSyncs(@Param("now") LocalDateTime now);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platform = :platform AND sl.syncStatus = 'FAILED' AND sl.retryCount < sl.maxRetryCount")
    List<SyncLog> findFailedSyncsByPlatform(@Param("platform") Platform platform);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus IN ('PENDING', 'IN_PROGRESS')")
    List<SyncLog> findActiveSyncs();
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.platform = :platform AND sl.createdAt BETWEEN :startDate AND :endDate")
    List<SyncLog> findByPlatformAndDateRange(@Param("platform") Platform platform, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(sl) FROM SyncLog sl WHERE sl.platform = :platform AND sl.syncStatus = :status AND sl.createdAt BETWEEN :startDate AND :endDate")
    long countByPlatformAndStatusAndDateRange(@Param("platform") Platform platform, 
                                             @Param("status") SyncLog.SyncStatus status,
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus = 'SUCCESS' AND sl.createdAt >= :since ORDER BY sl.createdAt DESC")
    List<SyncLog> findRecentSuccessfulSyncs(@Param("since") LocalDateTime since);
    
    @Query("SELECT sl FROM SyncLog sl WHERE sl.syncStatus = 'FAILED' AND sl.createdAt >= :since ORDER BY sl.createdAt DESC")
    List<SyncLog> findRecentFailedSyncs(@Param("since") LocalDateTime since);
}
