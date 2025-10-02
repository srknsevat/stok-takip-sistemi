package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.MaterialStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaterialStockMovementRepository extends JpaRepository<MaterialStockMovement, Long> {
    
    List<MaterialStockMovement> findByMaterialCardId(Long materialId);
    
    List<MaterialStockMovement> findByMovementType(String movementType);
    
    List<MaterialStockMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MaterialStockMovement> findByMaterialCardIdAndMovementType(Long materialId, String movementType);
    
    List<MaterialStockMovement> findByMovementDateAfter(LocalDateTime date);
    
    List<MaterialStockMovement> findByMovementDateBefore(LocalDateTime date);
}