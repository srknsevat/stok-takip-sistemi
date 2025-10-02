package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.MaterialCard;
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
    
    List<PlatformProduct> findByPlatform(Platform platform);
    
    List<PlatformProduct> findByProduct(MaterialCard product);
    
    List<PlatformProduct> findByPlatformIdAndIsActiveTrue(Long platformId);
    
    List<PlatformProduct> findByProductIdAndIsActiveTrue(Long productId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId ORDER BY pp.lastSyncAt DESC")
    List<PlatformProduct> findByPlatformIdOrderByLastSync(@Param("platformId") Long platformId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.product.id = :productId ORDER BY pp.lastSyncAt DESC")
    List<PlatformProduct> findByProductIdOrderByLastSync(@Param("productId") Long productId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.lastSyncAt < :date")
    List<PlatformProduct> findOutdatedProducts(@Param("date") LocalDateTime date);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.lastSyncAt < :date")
    List<PlatformProduct> findOutdatedProductsByPlatform(@Param("platformId") Long platformId, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.platform.id = :platformId")
    long countByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pp) FROM PlatformProduct pp WHERE pp.isActive = true")
    long countActiveProducts();
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platformSku = :sku")
    List<PlatformProduct> findByPlatformSku(@Param("sku") String sku);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platformProductId = :platformProductId")
    List<PlatformProduct> findByPlatformProductId(@Param("platformProductId") String platformProductId);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.platformSku = :sku")
    List<PlatformProduct> findByPlatformIdAndSku(@Param("platformId") Long platformId, @Param("sku") String sku);
    
    @Query("SELECT pp FROM PlatformProduct pp WHERE pp.platform.id = :platformId AND pp.platformProductId = :platformProductId")
    List<PlatformProduct> findByPlatformIdAndProductId(@Param("platformId") Long platformId, @Param("platformProductId") String platformProductId);
}