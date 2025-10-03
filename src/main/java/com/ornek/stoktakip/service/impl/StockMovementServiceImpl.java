package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.StockMovement;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.repository.StockMovementRepository;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final MaterialCardRepository materialCardRepository;

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                  MaterialCardRepository materialCardRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.materialCardRepository = materialCardRepository;
    }

    @Override
    public StockMovement createStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByMaterialId(Long materialId) {
        return stockMovementRepository.findByMaterialId(materialId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByMovementDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getStockMovementsByType(String movementType) {
        return stockMovementRepository.findByMovementType(movementType);
    }

    @Override
    public StockMovement createStockMovement(MaterialCard material, Integer quantity, String movementType, String description) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setMaterial(material);
        stockMovement.setQuantity(quantity);
        stockMovement.setMovementType(movementType);
        stockMovement.setDescription(description);
        stockMovement.setMovementDate(LocalDateTime.now());
        
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public StockMovement updateStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.save(stockMovement);
    }

    @Override
    public void deleteStockMovement(Long id) {
        stockMovementRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStockMovementStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<StockMovement> allMovements = stockMovementRepository.findAll();
        
        stats.put("totalMovements", allMovements.size());
        stats.put("totalQuantity", allMovements.stream()
            .mapToInt(StockMovement::getQuantity)
            .sum());
        
        // Hareket türüne göre gruplama
        Map<String, Long> movementsByType = new HashMap<>();
        for (StockMovement movement : allMovements) {
            movementsByType.merge(movement.getMovementType(), 1L, Long::sum);
        }
        stats.put("movementsByType", movementsByType);
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovement> getMaterialStockHistory(Long materialId) {
        return stockMovementRepository.findByMaterialIdOrderByMovementDateDesc(materialId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStockMovementReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = stockMovementRepository.findByMovementDateBetween(startDate, endDate);
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (StockMovement movement : movements) {
            Map<String, Object> reportItem = new HashMap<>();
            reportItem.put("id", movement.getId());
            reportItem.put("materialName", movement.getMaterial().getMaterialName());
            reportItem.put("quantity", movement.getQuantity());
            reportItem.put("movementType", movement.getMovementType());
            reportItem.put("description", movement.getDescription());
            reportItem.put("movementDate", movement.getMovementDate());
            report.add(reportItem);
        }
        
        return report;
    }
}
