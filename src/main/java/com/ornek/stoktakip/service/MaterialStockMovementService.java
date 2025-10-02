package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialStockMovement;
import java.time.LocalDateTime;
import java.util.List;

public interface MaterialStockMovementService {
    
    List<MaterialStockMovement> getAllMovements();
    
    List<MaterialStockMovement> getMovementsByMaterial(Long materialId);
    
    MaterialStockMovement getMovementById(Long id);
    
    MaterialStockMovement saveMovement(MaterialStockMovement movement);
    
    void deleteMovement(Long id);
    
    List<MaterialStockMovement> getMovementsByType(String movementType);
    
    List<MaterialStockMovement> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
