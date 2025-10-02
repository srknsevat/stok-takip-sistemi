package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.StockMovement;
import com.ornek.stoktakip.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;

    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDesc();
    }

    public void saveMovement(StockMovement movement) {
        stockMovementRepository.save(movement);
    }
} 