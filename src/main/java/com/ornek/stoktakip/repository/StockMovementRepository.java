package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    void deleteByProductId(Long productId);
    List<StockMovement> findAllByOrderByMovementDateDesc();
    
    // Service'de kullanılan metodlar
    List<StockMovement> findByMaterialId(Long materialId);
    List<StockMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<StockMovement> findByMovementType(String movementType);
    List<StockMovement> findByMaterialIdOrderByMovementDateDesc(Long materialId);
} 