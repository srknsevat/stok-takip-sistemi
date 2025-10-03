package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.BomType;
import com.ornek.stoktakip.repository.BillOfMaterialRepository;
import com.ornek.stoktakip.service.BillOfMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class BillOfMaterialServiceImpl implements BillOfMaterialService {
    
    private final BillOfMaterialRepository bomRepository;
    
    @Autowired
    public BillOfMaterialServiceImpl(BillOfMaterialRepository bomRepository) {
        this.bomRepository = bomRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getAllBOMs() {
        return bomRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BillOfMaterial getBOMById(Long id) {
        return bomRepository.findById(id).orElse(null);
    }
    
    @Override
    public BillOfMaterial saveBOM(BillOfMaterial bom) {
        if (bom.getId() == null) {
            bom.setCreatedAt(LocalDateTime.now());
        }
        bom.setUpdatedAt(LocalDateTime.now());
        return bomRepository.save(bom);
    }
    
    @Override
    public BillOfMaterial updateBOM(BillOfMaterial bom) {
        BillOfMaterial existingBOM = bomRepository.findById(bom.getId())
                .orElseThrow(() -> new RuntimeException("BOM bulunamadı: " + bom.getId()));
        
        bom.setUpdatedAt(LocalDateTime.now());
        return bomRepository.save(bom);
    }
    
    @Override
    public void deleteBOM(Long id) {
        bomRepository.deleteById(id);
    }
    
    @Override
    public BillOfMaterial createBOM(MaterialCard parentMaterial, MaterialCard childMaterial, 
                                   BigDecimal quantity, BomType bomType) {
        BillOfMaterial bom = new BillOfMaterial(parentMaterial, childMaterial, quantity);
        bom.setBomType(bomType);
        bom.setCreatedAt(LocalDateTime.now());
        bom.setUpdatedAt(LocalDateTime.now());
        
        return bomRepository.save(bom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMsByParentMaterial(Long parentMaterialId) {
        return bomRepository.findByParentMaterialId(parentMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMsByChildMaterial(Long childMaterialId) {
        return bomRepository.findByChildMaterialId(childMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMsByType(BomType bomType) {
        return bomRepository.findByBomType(bomType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getActiveBOMs() {
        return bomRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getEffectiveBOMs() {
        return bomRepository.findAll(); // Placeholder - gerçek implementasyon gerekli
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMStructure(Long parentMaterialId) {
        return bomRepository.findBOMStructureByParent(parentMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMStructureWithLevels(Long parentMaterialId) {
        return bomRepository.findBOMStructureByParent(parentMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBOMTree(Long parentMaterialId) {
        List<BillOfMaterial> structure = getBOMStructure(parentMaterialId);
        Map<String, Object> tree = new HashMap<>();
        
        // BOM ağacını oluştur
        for (BillOfMaterial bom : structure) {
            String key = bom.getParentMaterial().getMaterialCode() + "->" + bom.getChildMaterial().getMaterialCode();
            tree.put(key, Map.of(
                "parent", bom.getParentMaterial().getMaterialCode(),
                "child", bom.getChildMaterial().getMaterialCode(),
                "quantity", bom.getQuantity(),
                "level", bom.getBomLevel()
            ));
        }
        
        return tree;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getDirectComponents(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialIdAndBomLevel(parentMaterialId, 1);
        return boms.stream()
                .map(BillOfMaterial::getChildMaterial)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getAllComponents(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        return boms.stream()
                .map(BillOfMaterial::getChildMaterial)
                .distinct()
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getWhereUsed(Long childMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByChildMaterialId(childMaterialId);
        return boms.stream()
                .map(BillOfMaterial::getParentMaterial)
                .distinct()
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateMaterialRequirements(Long parentMaterialId, BigDecimal quantity) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        Map<String, BigDecimal> requirements = new HashMap<>();
        
        for (BillOfMaterial bom : boms) {
            String materialCode = bom.getChildMaterial().getMaterialCode();
            BigDecimal requiredQuantity = bom.getQuantity().multiply(quantity);
            
            requirements.merge(materialCode, requiredQuantity, BigDecimal::add);
        }
        
        return requirements;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateCostedBOM(Long parentMaterialId, BigDecimal quantity) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        Map<String, BigDecimal> costedBOM = new HashMap<>();
        
        for (BillOfMaterial bom : boms) {
            String materialCode = bom.getChildMaterial().getMaterialCode();
            BigDecimal requiredQuantity = bom.getQuantity().multiply(quantity);
            BigDecimal totalCost = requiredQuantity.multiply(bom.getUnitCost());
            
            costedBOM.merge(materialCode, totalCost, BigDecimal::add);
        }
        
        return costedBOM;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalCost(Long parentMaterialId, BigDecimal quantity) {
        Map<String, BigDecimal> costedBOM = calculateCostedBOM(parentMaterialId, quantity);
        return costedBOM.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalTime(Long parentMaterialId, BigDecimal quantity) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        BigDecimal totalTime = BigDecimal.ZERO;
        
        for (BillOfMaterial bom : boms) {
            BigDecimal bomTime = bom.getTotalTime().multiply(quantity);
            totalTime = totalTime.add(bomTime);
        }
        
        return totalTime;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateBOMStructure(Long parentMaterialId) {
        // BOM yapısı doğrulama
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        // Miktar kontrolü
        for (BillOfMaterial bom : boms) {
            if (bom.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasCircularReference(Long parentMaterialId) {
        // Dairesel referans kontrolü
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            if (bom.getChildMaterial().getId().equals(parentMaterialId)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getCircularReferences(Long parentMaterialId) {
        List<String> circularRefs = new ArrayList<>();
        
        if (hasCircularReference(parentMaterialId)) {
            circularRefs.add("Dairesel referans tespit edildi: " + parentMaterialId);
        }
        
        return circularRefs;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isBOMComplete(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        return !boms.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getMissingComponents(Long parentMaterialId) {
        // Eksik bileşenler kontrolü
        return new ArrayList<>();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> compareBOMs(Long bom1Id, Long bom2Id) {
        BillOfMaterial bom1 = bomRepository.findById(bom1Id).orElse(null);
        BillOfMaterial bom2 = bomRepository.findById(bom2Id).orElse(null);
        
        if (bom1 == null || bom2 == null) {
            return new ArrayList<>();
        }
        
        return List.of(bom1, bom2);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBOMDifferences(Long bom1Id, Long bom2Id) {
        Map<String, Object> differences = new HashMap<>();
        
        BillOfMaterial bom1 = bomRepository.findById(bom1Id).orElse(null);
        BillOfMaterial bom2 = bomRepository.findById(bom2Id).orElse(null);
        
        if (bom1 != null && bom2 != null) {
            differences.put("bom1", bom1);
            differences.put("bom2", bom2);
            differences.put("quantityDifference", bom1.getQuantity().subtract(bom2.getQuantity()));
        }
        
        return differences;
    }
    
    @Override
    public BillOfMaterial createBOMVersion(Long originalBOMId, String version) {
        BillOfMaterial originalBOM = bomRepository.findById(originalBOMId)
                .orElseThrow(() -> new RuntimeException("BOM bulunamadı: " + originalBOMId));
        
        BillOfMaterial newVersion = new BillOfMaterial(
            originalBOM.getParentMaterial(),
            originalBOM.getChildMaterial(),
            originalBOM.getQuantity()
        );
        
        newVersion.setBomType(originalBOM.getBomType());
        newVersion.setAlternativeBom(version);
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setUpdatedAt(LocalDateTime.now());
        
        return bomRepository.save(newVersion);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMVersions(Long parentMaterialId) {
        return bomRepository.findBOMVersions(parentMaterialId, BomType.MANUFACTURING);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BillOfMaterial getActiveBOMVersion(Long parentMaterialId) {
        List<BillOfMaterial> versions = getBOMVersions(parentMaterialId);
        return versions.isEmpty() ? null : versions.get(0);
    }
    
    @Override
    public void activateBOMVersion(Long bomId) {
        BillOfMaterial bom = bomRepository.findById(bomId)
                .orElseThrow(() -> new RuntimeException("BOM bulunamadı: " + bomId));
        
        bom.setIsActive(true);
        bom.setUpdatedAt(LocalDateTime.now());
        
        bomRepository.save(bom);
    }
    
    @Override
    public BillOfMaterial copyBOM(Long sourceBOMId, MaterialCard newParentMaterial) {
        BillOfMaterial sourceBOM = bomRepository.findById(sourceBOMId)
                .orElseThrow(() -> new RuntimeException("Kaynak BOM bulunamadı: " + sourceBOMId));
        
        BillOfMaterial newBOM = new BillOfMaterial(
            newParentMaterial,
            sourceBOM.getChildMaterial(),
            sourceBOM.getQuantity()
        );
        
        newBOM.setBomType(sourceBOM.getBomType());
        newBOM.setBomLevel(sourceBOM.getBomLevel());
        newBOM.setCreatedAt(LocalDateTime.now());
        newBOM.setUpdatedAt(LocalDateTime.now());
        
        return bomRepository.save(newBOM);
    }
    
    @Override
    public BillOfMaterial copyBOMStructure(Long sourceParentId, MaterialCard newParentMaterial) {
        List<BillOfMaterial> sourceBOMs = bomRepository.findByParentMaterialId(sourceParentId);
        BillOfMaterial firstBOM = null;
        
        for (BillOfMaterial sourceBOM : sourceBOMs) {
            BillOfMaterial newBOM = copyBOM(sourceBOM.getId(), newParentMaterial);
            if (firstBOM == null) {
                firstBOM = newBOM;
            }
        }
        
        return firstBOM;
    }
    
    @Override
    public void updateBOMQuantities(Long parentMaterialId, Map<Long, BigDecimal> quantityUpdates) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            if (quantityUpdates.containsKey(bom.getId())) {
                bom.setQuantity(quantityUpdates.get(bom.getId()));
                bom.setUpdatedAt(LocalDateTime.now());
                bomRepository.save(bom);
            }
        }
    }
    
    @Override
    public void updateBOMCosts(Long parentMaterialId, Map<Long, BigDecimal> costUpdates) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            if (costUpdates.containsKey(bom.getId())) {
                bom.setUnitCost(costUpdates.get(bom.getId()));
                bom.setUpdatedAt(LocalDateTime.now());
                bomRepository.save(bom);
            }
        }
    }
    
    @Override
    public void deactivateBOMs(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            bom.setIsActive(false);
            bom.setUpdatedAt(LocalDateTime.now());
            bomRepository.save(bom);
        }
    }
    
    @Override
    public void activateBOMs(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            bom.setIsActive(true);
            bom.setUpdatedAt(LocalDateTime.now());
            bomRepository.save(bom);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBOMReport(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (BillOfMaterial bom : boms) {
            Map<String, Object> item = new HashMap<>();
            item.put("parentMaterial", bom.getParentMaterial().getMaterialCode());
            item.put("childMaterial", bom.getChildMaterial().getMaterialCode());
            item.put("quantity", bom.getQuantity());
            item.put("unitCost", bom.getUnitCost());
            item.put("totalCost", bom.getTotalCost());
            item.put("bomLevel", bom.getBomLevel());
            report.add(item);
        }
        
        return report;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCostedBOMReport(Long parentMaterialId) {
        return getBOMReport(parentMaterialId); // Placeholder
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWhereUsedReport(Long childMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByChildMaterialId(childMaterialId);
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (BillOfMaterial bom : boms) {
            Map<String, Object> item = new HashMap<>();
            item.put("parentMaterial", bom.getParentMaterial().getMaterialCode());
            item.put("childMaterial", bom.getChildMaterial().getMaterialCode());
            item.put("quantity", bom.getQuantity());
            item.put("bomLevel", bom.getBomLevel());
            report.add(item);
        }
        
        return report;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBOMComparisonReport(Long bom1Id, Long bom2Id) {
        Map<String, Object> differences = getBOMDifferences(bom1Id, bom2Id);
        return List.of(differences);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getBOMCount() {
        return bomRepository.countTotalBOMs();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getBOMCountByType(BomType bomType) {
        return bomRepository.countByBomType(bomType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getBOMCountByParent(Long parentMaterialId) {
        return bomRepository.countByParent(parentMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getBOMCountByChild(Long childMaterialId) {
        return bomRepository.countByChild(childMaterialId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateBOMLevels(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        return boms.stream().allMatch(bom -> bom.getBomLevel() > 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateBOMQuantities(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        return boms.stream().allMatch(bom -> bom.getQuantity().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateBOMDates(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        return boms.stream().allMatch(bom -> 
            bom.getEffectiveFrom() == null || bom.getEffectiveTo() == null || 
            bom.getEffectiveFrom().isBefore(bom.getEffectiveTo())
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getBOMValidationErrors(Long parentMaterialId) {
        List<String> errors = new ArrayList<>();
        
        if (!validateBOMLevels(parentMaterialId)) {
            errors.add("Geçersiz BOM seviyeleri");
        }
        
        if (!validateBOMQuantities(parentMaterialId)) {
            errors.add("Geçersiz BOM miktarları");
        }
        
        if (!validateBOMDates(parentMaterialId)) {
            errors.add("Geçersiz BOM tarihleri");
        }
        
        return errors;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> searchBOMs(String searchTerm) {
        return bomRepository.findBySearchTerm(searchTerm);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMsByOperation(String operationName) {
        return bomRepository.findByOperationName(operationName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BillOfMaterial> getBOMsByWorkCenter(String workCenter) {
        return bomRepository.findByWorkCenter(workCenter);
    }
    
    @Override
    public List<BillOfMaterial> importBOMFromExcel(String filePath) {
        // Excel import implementasyonu
        return new ArrayList<>();
    }
    
    @Override
    public void exportBOMToExcel(Long parentMaterialId, String filePath) {
        // Excel export implementasyonu
    }
    
    @Override
    public void updateBOMCosts(Long parentMaterialId) {
        List<BillOfMaterial> boms = bomRepository.findByParentMaterialId(parentMaterialId);
        
        for (BillOfMaterial bom : boms) {
            // Maliyet güncelleme mantığı
            bom.setUpdatedAt(LocalDateTime.now());
            bomRepository.save(bom);
        }
    }
    
    @Override
    public void recalculateAllBOMCosts() {
        List<BillOfMaterial> allBOMs = bomRepository.findAll();
        
        for (BillOfMaterial bom : allBOMs) {
            updateBOMCosts(bom.getParentMaterial().getId());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getBOMCostBreakdown(Long parentMaterialId) {
        return calculateCostedBOM(parentMaterialId, BigDecimal.ONE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateMRPRequirements(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate) {
        return calculateMaterialRequirements(parentMaterialId, quantity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMRPReport(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate) {
        Map<String, BigDecimal> requirements = calculateMRPRequirements(parentMaterialId, quantity, dueDate);
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (Map.Entry<String, BigDecimal> entry : requirements.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("materialCode", entry.getKey());
            item.put("requiredQuantity", entry.getValue());
            item.put("dueDate", dueDate);
            report.add(item);
        }
        
        return report;
    }
}
