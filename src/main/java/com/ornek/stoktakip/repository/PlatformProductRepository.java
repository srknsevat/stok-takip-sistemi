package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.PlatformProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlatformProductRepository extends JpaRepository<PlatformProduct, Long> {
    
    List<PlatformProduct> findByPlatformId(Long platformId);
    
    List<PlatformProduct> findByProductId(Long productId);
    
    List<PlatformProduct> findByPlatformIdAndProductId(Long platformId, Long productId);
    
    List<PlatformProduct> findByPlatformProductId(String platformProductId);
    
    List<PlatformProduct> findByPlatformSku(String platformSku);
    
    List<PlatformProduct> findByPlatformStatus(String platformStatus);
    
    List<PlatformProduct> findByIsSynced(Boolean isSynced);
    
    List<PlatformProduct> findByLastSyncAtBefore(LocalDateTime date);
    
    List<PlatformProduct> findByLastSyncAtAfter(LocalDateTime date);
    
    List<PlatformProduct> findByLastSyncAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.isSynced = false")
    List<PlatformProduct> findUnsyncedByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.product.id = :productId AND pp.isSynced = false")
    List<PlatformProduct> findUnsyncedByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.platformStatus = :status")
    List<PlatformProduct> findByPlatformIdAndStatus(@Param("platformId") Long platformId, @Param("status") String status);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform.id = :platformId")
    Long countByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.isSynced = true")
    Long countSyncedByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.isSynced = false")
    Long countUnsyncedByPlatformId(@Param("platformId") Long platformId);
}