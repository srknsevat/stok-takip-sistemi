package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformProductRepository extends JpaRepository<PlatformProduct, Long> {
    
    Optional<PlatformProduct> findByProductAndPlatform(Product product, Platform platform);
    
    Optional<PlatformProduct> findByPlatformProductIdAndPlatform(String platformProductId, Platform platform);
    
    List<PlatformProduct> findByProduct(Product product);
    
    List<PlatformProduct> findByPlatform(Platform platform);
    
    List<PlatformProduct> findByPlatformAndIsActiveTrue(Platform platform);
    
    List<PlatformProduct> findByProductAndIsActiveTrue(Product product);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.isActive = true AND pp.isSynced = false")
    List<PlatformProduct> findUnsyncedByPlatform(@Param("platform") Platform platform);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.product = :product AND pp.isActive = true AND pp.isSynced = false")
    List<PlatformProduct> findUnsyncedByProduct(@Param("product") Product product);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.product = :product AND pp.isActive = true")
    Optional<PlatformProduct> findActiveByPlatformAndProduct(@Param("platform") Platform platform, @Param("product") Product product);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.isActive = true")
    long countActiveByPlatform(@Param("platform") Platform platform);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.product = :product AND pp.isActive = true")
    long countActiveByProduct(@Param("product") Product product);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform = :platform AND pp.lastSyncAt IS NULL OR pp.lastSyncAt < :beforeDate")
    List<PlatformProduct> findNeedingSyncByPlatform(@Param("platform") Platform platform, @Param("beforeDate") java.time.LocalDateTime beforeDate);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.retryCount > 0 AND pp.retryCount < 3")
    List<PlatformProduct> findFailedSyncs();
}
