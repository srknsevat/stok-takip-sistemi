package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.repository.BillOfMaterialRepository;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.service.BomExplosionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class BomExplosionServiceImpl implements BomExplosionService {

    private final BillOfMaterialRepository bomRepository;
    private final MaterialCardRepository materialCardRepository;

    @Autowired
    public BomExplosionServiceImpl(BillOfMaterialRepository bomRepository, MaterialCardRepository materialCardRepository) {
        this.bomRepository = bomRepository;
        this.materialCardRepository = materialCardRepository;
    }

    @Override
    public Map<String, BomExplosionResult> explodeBOM(Long parentMaterialId, BigDecimal parentQuantity) {
        Map<String, BomExplosionResult> result = new HashMap<>();
        
        // BOM patlatma işlemi
        var boms = bomRepository.findByParentMaterialIdAndIsActiveTrue(parentMaterialId);
        
        for (var bom : boms) {
            BigDecimal requiredQuantity = bom.getQuantity().multiply(parentQuantity);
            String bomPath = bom.getParentMaterial().getMaterialName() + " -> " + bom.getComponentMaterial().getMaterialName();
            
            BomExplosionResult bomResult = new BomExplosionResult(
                bom.getComponentMaterial(),
                requiredQuantity,
                bom.getBomLevel(),
                bomPath
            );
            
            result.put(bom.getComponentMaterial().getMaterialName(), bomResult);
        }
        
        return result;
    }

    @Override
    public Map<Long, Double> explodeBom(Long parentMaterialId, double parentQuantity) {
        Map<Long, Double> result = new HashMap<>();
        
        // BOM patlatma işlemi
        var boms = bomRepository.findByParentMaterialIdAndIsActiveTrue(parentMaterialId);
        
        for (var bom : boms) {
            Double requiredQuantity = bom.getQuantity().doubleValue() * parentQuantity;
            result.put(bom.getComponentMaterial().getId(), requiredQuantity);
        }
        
        return result;
    }
}