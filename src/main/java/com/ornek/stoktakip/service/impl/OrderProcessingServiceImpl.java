package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.MaterialStockMovement;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.service.BomExplosionService;
import com.ornek.stoktakip.service.MaterialStockMovementService;
import com.ornek.stoktakip.service.OrderProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderProcessingServiceImpl implements OrderProcessingService {

    private final MaterialCardRepository materialCardRepository;
    private final MaterialStockMovementService materialStockMovementService;
    private final BomExplosionService bomExplosionService;

    @Autowired
    public OrderProcessingServiceImpl(MaterialCardRepository materialCardRepository,
                                    MaterialStockMovementService materialStockMovementService,
                                    BomExplosionService bomExplosionService) {
        this.materialCardRepository = materialCardRepository;
        this.materialStockMovementService = materialStockMovementService;
        this.bomExplosionService = bomExplosionService;
    }

    @Override
    public boolean processOrder(Long materialId, BigDecimal quantity, String orderType) {
        try {
            MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
            if (material == null) {
                return false;
            }

            // Stok kontrolü
            if (!checkStockAvailability(materialId, quantity)) {
                return false;
            }

            // BOM patlatma ve stok düşürme
            if (!explodeBOMAndDeductStock(materialId, quantity)) {
                return false;
            }

            // Stok hareketi oluştur
            createStockMovement(materialId, quantity, "OUT", "Sipariş işleme: " + orderType);

            return true;
        } catch (Exception e) {
            System.err.println("Sipariş işleme hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelOrder(Long materialId, BigDecimal quantity, String orderType) {
        try {
            // BOM patlatma ve stok artırma
            if (!explodeBOMAndAddStock(materialId, quantity)) {
                return false;
            }

            // Stok hareketi oluştur
            createStockMovement(materialId, quantity, "IN", "Sipariş iptal: " + orderType);

            return true;
        } catch (Exception e) {
            System.err.println("Sipariş iptal hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean returnOrder(Long materialId, BigDecimal quantity, String orderType) {
        try {
            // BOM patlatma ve stok artırma
            if (!explodeBOMAndAddStock(materialId, quantity)) {
                return false;
            }

            // Stok hareketi oluştur
            createStockMovement(materialId, quantity, "IN", "Sipariş iade: " + orderType);

            return true;
        } catch (Exception e) {
            System.err.println("Sipariş iade hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean createStockMovement(Long materialId, BigDecimal quantity, String movementType, String description) {
        try {
            MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
            if (material == null) {
                return false;
            }

            MaterialStockMovement movement = new MaterialStockMovement();
            movement.setMaterialCard(material);
            movement.setMovementType(movementType);
            movement.setQuantity(quantity);
            movement.setMovementDate(LocalDateTime.now());
            movement.setDescription(description);

            materialStockMovementService.saveMovement(movement);
            return true;
        } catch (Exception e) {
            System.err.println("Stok hareketi oluşturma hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean explodeBOMAndDeductStock(Long materialId, BigDecimal quantity) {
        try {
            // BOM'u patlat
            Map<MaterialCard, Double> explosionResults = bomExplosionService.explodeBom(materialId, quantity.doubleValue());
            
            if (explosionResults.isEmpty()) {
                // BOM yoksa, sadece ana malzemeden düş
                return deductStock(materialId, quantity);
            }

            // Her alt parçadan stok düş
            for (Map.Entry<MaterialCard, Double> entry : explosionResults.entrySet()) {
                MaterialCard childMaterial = entry.getKey();
                Double requiredQuantityDouble = entry.getValue();
                BigDecimal requiredQuantity = BigDecimal.valueOf(requiredQuantityDouble);
                
                if (!deductStock(childMaterial.getId(), requiredQuantity)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("BOM patlatma ve stok düşürme hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkStockAvailability(Long materialId, BigDecimal quantity) {
        try {
            MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
            if (material == null) {
                return false;
            }

            return material.getCurrentStock().compareTo(quantity) >= 0;
        } catch (Exception e) {
            System.err.println("Stok kontrol hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getOrderHistory(Long materialId) {
        // Bu kısım Order entity'sinden gelecek
        // Şimdilik boş liste döndürüyoruz
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getStockMovementHistory(Long materialId) {
        try {
            List<MaterialStockMovement> movements = materialStockMovementService.getMovementsByMaterial(materialId);
            List<Map<String, Object>> history = new ArrayList<>();
            
            for (MaterialStockMovement movement : movements) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", movement.getId());
                record.put("movementType", movement.getMovementType());
                record.put("quantity", movement.getQuantity());
                record.put("movementDate", movement.getMovementDate());
                record.put("description", movement.getDescription());
                history.add(record);
            }
            
            return history;
        } catch (Exception e) {
            System.err.println("Stok hareket geçmişi hatası: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean deductStock(Long materialId, BigDecimal quantity) {
        try {
            MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
            if (material == null) {
                return false;
            }

            BigDecimal newStock = material.getCurrentStock().subtract(quantity);
            if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            material.setCurrentStock(newStock);
            materialCardRepository.save(material);
            return true;
        } catch (Exception e) {
            System.err.println("Stok düşürme hatası: " + e.getMessage());
            return false;
        }
    }

    private boolean explodeBOMAndAddStock(Long materialId, BigDecimal quantity) {
        try {
            // BOM'u patlat
            Map<MaterialCard, Double> explosionResults = bomExplosionService.explodeBom(materialId, quantity.doubleValue());
            
            if (explosionResults.isEmpty()) {
                // BOM yoksa, sadece ana malzemeye ekle
                return addStock(materialId, quantity);
            }

            // Her alt parçaya stok ekle
            for (Map.Entry<MaterialCard, Double> entry : explosionResults.entrySet()) {
                MaterialCard childMaterial = entry.getKey();
                Double requiredQuantityDouble = entry.getValue();
                BigDecimal requiredQuantity = BigDecimal.valueOf(requiredQuantityDouble);
                
                if (!addStock(childMaterial.getId(), requiredQuantity)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("BOM patlatma ve stok ekleme hatası: " + e.getMessage());
            return false;
        }
    }

    private boolean addStock(Long materialId, BigDecimal quantity) {
        try {
            MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
            if (material == null) {
                return false;
            }

            BigDecimal newStock = material.getCurrentStock().add(quantity);
            material.setCurrentStock(newStock);
            materialCardRepository.save(material);
            return true;
        } catch (Exception e) {
            System.err.println("Stok ekleme hatası: " + e.getMessage());
            return false;
        }
    }
}
