package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    
    Optional<Platform> findByCode(String code);
    
    Optional<Platform> findByName(String name);
    
    List<Platform> findByIsActiveTrue();
    
    List<Platform> findBySyncEnabledTrue();
    
    @Query("SELECT p FROM Platform p WHERE p.isActive = true AND p.syncEnabled = true")
    List<Platform> findActiveAndSyncEnabledPlatforms();
    
    @Query("SELECT p FROM Platform p WHERE p.code = :code AND p.isActive = true")
    Optional<Platform> findActiveByCode(@Param("code") String code);
    
    @Query("SELECT COUNT(p) FROM Platform p WHERE p.isActive = true")
    long countActivePlatforms();
    
    @Query("SELECT p FROM Platform p WHERE p.lastSyncAt IS NULL OR p.lastSyncAt < :beforeDate")
    List<Platform> findPlatformsNeedingSync(@Param("beforeDate") java.time.LocalDateTime beforeDate);
}
