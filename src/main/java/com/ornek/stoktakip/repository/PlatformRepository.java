package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    
    List<Platform> findByIsActiveTrue();
    
    List<Platform> findByIsActiveFalse();
    
    List<Platform> findBySyncEnabledTrue();
    
    List<Platform> findBySyncEnabledFalse();
    
    List<Platform> findByCode(String code);
    
    List<Platform> findByNameContainingIgnoreCase(String name);
    
    Optional<Platform> findByCodeAndIsActiveTrue(String code);
    
    Optional<Platform> findByNameAndIsActiveTrue(String name);
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.syncEnabled = true")
    List<Platform> findActiveAndSyncEnabled();
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.lastSyncAt IS NULL")
    List<Platform> findActiveNeverSynced();
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.lastSyncAt < :date")
    List<Platform> findActiveWithOldSync(@Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.tokenExpiresAt < :date")
    List<Platform> findActiveWithExpiredTokens(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(p) FROM Platform p WHERE p.isActive = true")
    Long countActivePlatforms();
    
    @Query("SELECT COUNT(p) FROM Platform p WHERE p.isActive = true AND p.syncEnabled = true")
    Long countActiveAndSyncEnabled();
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.syncEnabled = true AND (p.lastSyncAt IS NULL OR p.lastSyncAt < :date)")
    List<Platform> findPlatformsNeedingSync(@Param("date") LocalDateTime date);
}