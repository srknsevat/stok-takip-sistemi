package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.MaterialStockMovement;
import com.ornek.stoktakip.repository.MaterialStockMovementRepository;
import com.ornek.stoktakip.service.MaterialStockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialStockMovementServiceImpl implements MaterialStockMovementService {

    private final MaterialStockMovementRepository materialStockMovementRepository;

    @Autowired
    public MaterialStockMovementServiceImpl(MaterialStockMovementRepository materialStockMovementRepository) {
        this.materialStockMovementRepository = materialStockMovementRepository;
    }

    @Override
    public List<MaterialStockMovement> getAllMovements() {
        return materialStockMovementRepository.findAll();
    }

    @Override
    public List<MaterialStockMovement> getMovementsByMaterial(Long materialId) {
        return materialStockMovementRepository.findByMaterialCardId(materialId);
    }

    @Override
    public MaterialStockMovement getMovementById(Long id) {
        return materialStockMovementRepository.findById(id).orElse(null);
    }

    @Override
    public MaterialStockMovement saveMovement(MaterialStockMovement movement) {
        if (movement.getMovementDate() == null) {
            movement.setMovementDate(LocalDateTime.now());
        }
        return materialStockMovementRepository.save(movement);
    }

    @Override
    public void deleteMovement(Long id) {
        materialStockMovementRepository.deleteById(id);
    }

    @Override
    public List<MaterialStockMovement> getMovementsByType(String movementType) {
        return materialStockMovementRepository.findByMovementType(movementType);
    }

    @Override
    public List<MaterialStockMovement> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return materialStockMovementRepository.findByMovementDateBetween(startDate, endDate);
    }
}
