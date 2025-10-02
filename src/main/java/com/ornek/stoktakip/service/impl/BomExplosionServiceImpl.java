package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.MaterialStockMovement;
import com.ornek.stoktakip.repository.BillOfMaterialRepository;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.repository.MaterialStockMovementRepository;
import com.ornek.stoktakip.service.BomExplosionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BomExplosionServiceImpl implements BomExplosionService {
    
    private final BillOfMaterialRepository bomRepository;
    private final MaterialCardRepository materialCardRepository;
    private final MaterialStockMovementRepository stockMovementRepository;
    
    @Autowired
    public BomExplosionServiceImpl(BillOfMaterialRepository bomRepository,
                                 MaterialCardRepository materialCardRepository,
                                 MaterialStockMovementRepository stockMovementRepository) {
        this.bomRepository = bomRepository;
        this.materialCardRepository = materialCardRepository;
        this.stockMovementRepository = stockMovementRepository;
    }
    
    @Override
    public Map<String, BomExplosionResult> explodeBOM(Long parentMaterialId, BigDecimal quantity) {
        Map<String, BomExplosionResult> explosionResults = new HashMap<>();
        MaterialCard parentMaterial = materialCardRepository.findById(parentMaterialId).orElse(null);
        
        if (parentMaterial == null) {
            return explosionResults;
        }
        
        // BOM'u recursive olarak patlat
        explodeBOMRecursive(parentMaterialId, quantity, explosionResults, "", 1);
        
        return explosionResults;
    }
    
    private void explodeBOMRecursive(Long parentMaterialId, BigDecimal quantity, 
                                   Map<String, BomExplosionResult> explosionResults, 
                                   String bomPath, Integer bomLevel) {
        
        // Bu malzemenin BOM'larını bul
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialIdAndIsActiveTrue(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            MaterialCard childMaterial = bom.getChildMaterial();
            String childMaterialCode = childMaterial.getMaterialCode();
            
            // Gerekli miktarı hesapla (verimlilik ve fire hesabı ile)
            BigDecimal requiredQuantity = bom.getEffectiveQuantity().multiply(quantity);
            
            // BOM path'i oluştur
            String childBomPath = bomPath.isEmpty() ? childMaterialCode : bomPath + " -> " + childMaterialCode;
            
            // Eğer bu malzeme daha önce hesaplanmışsa, miktarları topla
            if (explosionResults.containsKey(childMaterialCode)) {
                BomExplosionResult existingResult = explosionResults.get(childMaterialCode);
                existingResult.setRequiredQuantity(existingResult.getRequiredQuantity().add(requiredQuantity));
                existingResult.setTotalCost(existingResult.getTotalCost().add(requiredQuantity.multiply(childMaterial.getAverageCost())));
            } else {
                // Yeni sonuç oluştur
                BomExplosionResult result = new BomExplosionResult(childMaterial, requiredQuantity, childMaterial.getCurrentStock());
                result.setBomLevel(bomLevel);
                result.setBomPath(childBomPath);
                result.setUnitCost(childMaterial.getAverageCost());
                result.setTotalCost(requiredQuantity.multiply(childMaterial.getAverageCost()));
                result.setShortage(requiredQuantity.subtract(childMaterial.getCurrentStock()));
                
                explosionResults.put(childMaterialCode, result);
            }
            
            // Eğer bu malzemenin de BOM'u varsa, recursive olarak patlat
            if (hasBOM(childMaterial.getId())) {
                explodeBOMRecursive(childMaterial.getId(), requiredQuantity, explosionResults, childBomPath, bomLevel + 1);
            }
        }
    }
    
    @Override
    public boolean explodeBOMAndUpdateStock(Long parentMaterialId, BigDecimal quantity, String referenceNumber, String reason) {
        try {
            // BOM'u patlat ve stok hareketlerini oluştur
            List<MaterialStockMovement> stockMovements = createStockMovements(parentMaterialId, quantity, referenceNumber, reason);
            
            // Stok hareketlerini commit et
            return commitStockMovements(stockMovements);
            
        } catch (Exception e) {
            System.err.println("BOM explosion ve stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public BomStockCheckResult checkBOMStockAvailability(Long parentMaterialId, BigDecimal quantity) {
        Map<String, BomExplosionResult> explosionResults = explodeBOM(parentMaterialId, quantity);
        List<BomStockShortage> shortages = new ArrayList<>();
        boolean stockAvailable = true;
        BigDecimal totalShortageValue = BigDecimal.ZERO;
        
        for (BomExplosionResult result : explosionResults.values()) {
            if (result.hasShortage()) {
                stockAvailable = false;
                
                BomStockShortage shortage = new BomStockShortage(
                    result.getMaterial(),
                    result.getRequiredQuantity(),
                    result.getAvailableStock(),
                    result.getShortage()
                );
                shortage.setBomLevel(result.getBomLevel());
                shortage.setBomPath(result.getBomPath());
                shortage.setUnitCost(result.getUnitCost());
                shortage.setShortageValue(result.getShortage().multiply(result.getUnitCost()));
                
                shortages.add(shortage);
                totalShortageValue = totalShortageValue.add(shortage.getShortageValue());
            }
        }
        
        BomStockCheckResult checkResult = new BomStockCheckResult(stockAvailable, shortages);
        checkResult.setTotalShortageValue(totalShortageValue);
        
        if (stockAvailable) {
            checkResult.setMessage("Tüm malzemeler stokta mevcut");
        } else {
            checkResult.setMessage(shortages.size() + " malzemede stok yetersizliği var");
        }
        
        return checkResult;
    }
    
    @Override
    public List<BomStockShortage> getBOMStockShortages(Long parentMaterialId, BigDecimal quantity) {
        BomStockCheckResult checkResult = checkBOMStockAvailability(parentMaterialId, quantity);
        return checkResult.getShortages();
    }
    
    @Override
    public BomCostResult calculateBOMCost(Long parentMaterialId, BigDecimal quantity) {
        Map<String, BomExplosionResult> explosionResults = explodeBOM(parentMaterialId, quantity);
        BomCostResult costResult = new BomCostResult();
        
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, BigDecimal> costByLevel = new HashMap<>();
        
        for (BomExplosionResult result : explosionResults.values()) {
            totalCost = totalCost.add(result.getTotalCost());
            
            String levelKey = "Level " + result.getBomLevel();
            costByLevel.merge(levelKey, result.getTotalCost(), BigDecimal::add);
        }
        
        costResult.setTotalCost(totalCost);
        costResult.setTotalQuantity(quantity);
        costResult.setUnitCost(quantity.compareTo(BigDecimal.ZERO) > 0 ? totalCost.divide(quantity, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        costResult.setCostBreakdown(new ArrayList<>(explosionResults.values()));
        costResult.setCostByLevel(costByLevel);
        
        return costResult;
    }
    
    @Override
    public List<BomMRPItem> calculateMRP(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate) {
        Map<String, BomExplosionResult> explosionResults = explodeBOM(parentMaterialId, quantity);
        List<BomMRPItem> mrpItems = new ArrayList<>();
        
        for (BomExplosionResult result : explosionResults.values()) {
            MaterialCard material = result.getMaterial();
            
            BomMRPItem mrpItem = new BomMRPItem(material, result.getRequiredQuantity(), dueDate);
            mrpItem.setBomLevel(result.getBomLevel());
            mrpItem.setBomPath(result.getBomPath());
            
            // Malzeme türüne göre işlem türünü belirle
            if (material.getMaterialType() == MaterialCard.MaterialType.RAW_MATERIAL) {
                mrpItem.setPurchased(true);
                mrpItem.setLeadTime(BigDecimal.valueOf(material.getLeadTimeDays()));
            } else if (material.getMaterialType() == MaterialCard.MaterialType.SEMI_FINISHED || 
                      material.getMaterialType() == MaterialCard.MaterialType.FINISHED_GOOD) {
                mrpItem.setManufactured(true);
                mrpItem.setLeadTime(BigDecimal.valueOf(1)); // Varsayılan üretim süresi
            }
            
            mrpItems.add(mrpItem);
        }
        
        // BOM seviyesine göre sırala
        mrpItems.sort(Comparator.comparing(BomMRPItem::getBomLevel));
        
        return mrpItems;
    }
    
    @Override
    public BomProductionPlan createProductionPlan(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate) {
        MaterialCard parentMaterial = materialCardRepository.findById(parentMaterialId).orElse(null);
        if (parentMaterial == null) {
            return null;
        }
        
        BomProductionPlan plan = new BomProductionPlan();
        plan.setParentMaterial(parentMaterial);
        plan.setQuantity(quantity);
        plan.setDueDate(dueDate);
        
        List<BomMRPItem> mrpItems = calculateMRP(parentMaterialId, quantity, dueDate);
        plan.setMrpItems(mrpItems);
        
        BomCostResult costResult = calculateBOMCost(parentMaterialId, quantity);
        plan.setTotalCost(costResult.getTotalCost());
        
        // Başlangıç ve bitiş tarihlerini hesapla
        if (!mrpItems.isEmpty()) {
            LocalDateTime startDate = dueDate.minusDays(7); // Varsayılan 7 gün önceden başla
            plan.setStartDate(startDate);
            plan.setEndDate(dueDate);
        }
        
        return plan;
    }
    
    @Override
    public List<MaterialStockMovement> createStockMovements(Long parentMaterialId, BigDecimal quantity, String referenceNumber, String reason) {
        Map<String, BomExplosionResult> explosionResults = explodeBOM(parentMaterialId, quantity);
        List<MaterialStockMovement> stockMovements = new ArrayList<>();
        
        for (BomExplosionResult result : explosionResults.values()) {
            if (result.getRequiredQuantity().compareTo(BigDecimal.ZERO) > 0) {
                MaterialStockMovement movement = new MaterialStockMovement(
                    result.getMaterial(),
                    result.getRequiredQuantity(),
                    MaterialStockMovement.MovementType.PRODUCTION_OUT
                );
                
                movement.setReferenceNumber(referenceNumber);
                movement.setReferenceType("PRODUCTION");
                movement.setUnitCost(result.getUnitCost());
                movement.setTotalCost(result.getTotalCost());
                movement.setReason(reason);
                movement.setMovementDate(LocalDateTime.now());
                
                stockMovements.add(movement);
            }
        }
        
        return stockMovements;
    }
    
    @Override
    public boolean commitStockMovements(List<MaterialStockMovement> stockMovements) {
        try {
            for (MaterialStockMovement movement : stockMovements) {
                // Stok hareketini kaydet
                stockMovementRepository.save(movement);
                
                // Malzeme stokunu güncelle
                MaterialCard material = movement.getMaterial();
                BigDecimal newStock = material.getCurrentStock().subtract(movement.getQuantity());
                material.setCurrentStock(newStock);
                materialCardRepository.save(material);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Stok hareketleri commit hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean rollbackStockMovements(List<MaterialStockMovement> stockMovements) {
        try {
            for (MaterialStockMovement movement : stockMovements) {
                // Stok hareketini sil
                stockMovementRepository.delete(movement);
                
                // Malzeme stokunu geri yükle
                MaterialCard material = movement.getMaterial();
                BigDecimal newStock = material.getCurrentStock().add(movement.getQuantity());
                material.setCurrentStock(newStock);
                materialCardRepository.save(material);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Stok hareketleri rollback hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public BomExplosionReport generateExplosionReport(Long parentMaterialId, BigDecimal quantity) {
        MaterialCard parentMaterial = materialCardRepository.findById(parentMaterialId).orElse(null);
        if (parentMaterial == null) {
            return null;
        }
        
        BomExplosionReport report = new BomExplosionReport();
        report.setParentMaterial(parentMaterial);
        report.setQuantity(quantity);
        report.setExplosionDate(LocalDateTime.now());
        
        Map<String, BomExplosionResult> explosionResults = explodeBOM(parentMaterialId, quantity);
        report.setExplosionResults(new ArrayList<>(explosionResults.values()));
        
        BomCostResult costResult = calculateBOMCost(parentMaterialId, quantity);
        report.setCostResult(costResult);
        
        BomStockCheckResult stockCheckResult = checkBOMStockAvailability(parentMaterialId, quantity);
        report.setStockCheckResult(stockCheckResult);
        
        // Rapor özeti oluştur
        StringBuilder summary = new StringBuilder();
        summary.append("BOM Explosion Raporu\n");
        summary.append("Ana Malzeme: ").append(parentMaterial.getMaterialCode()).append(" - ").append(parentMaterial.getMaterialName()).append("\n");
        summary.append("Miktar: ").append(quantity).append("\n");
        summary.append("Toplam Maliyet: ").append(costResult.getTotalCost()).append(" TL\n");
        summary.append("Stok Durumu: ").append(stockCheckResult.isStockAvailable() ? "Yeterli" : "Yetersiz").append("\n");
        summary.append("Toplam Malzeme: ").append(explosionResults.size()).append(" adet\n");
        
        report.setReportSummary(summary.toString());
        
        return report;
    }
    
    private boolean hasBOM(Long materialId) {
        return bomRepository.countByParentMaterialIdAndIsActiveTrue(materialId) > 0;
    }
}
