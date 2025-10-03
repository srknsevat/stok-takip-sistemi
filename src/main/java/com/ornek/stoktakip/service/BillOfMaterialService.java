package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.BomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BillOfMaterialService {
    
    // Temel CRUD operasyonları
    List<BillOfMaterial> getAllBOMs();
    
    BillOfMaterial getBOMById(Long id);
    
    BillOfMaterial saveBOM(BillOfMaterial bom);
    
    BillOfMaterial updateBOM(BillOfMaterial bom);
    
    void deleteBOM(Long id);
    
    // BOM oluşturma
    BillOfMaterial createBOM(MaterialCard parentMaterial, MaterialCard childMaterial, 
                           BigDecimal quantity, BillOfMaterial.BomType bomType);
    
    // BOM sorguları
    List<BillOfMaterial> getBOMsByParentMaterial(Long parentMaterialId);
    
    List<BillOfMaterial> getBOMsByChildMaterial(Long childMaterialId);
    
    List<BillOfMaterial> getBOMsByType(BillOfMaterial.BomType bomType);
    
    List<BillOfMaterial> getActiveBOMs();
    
    List<BillOfMaterial> getEffectiveBOMs();
    
    // BOM yapısı
    List<BillOfMaterial> getBOMStructure(Long parentMaterialId);
    
    List<BillOfMaterial> getBOMStructureWithLevels(Long parentMaterialId);
    
    Map<String, Object> getBOMTree(Long parentMaterialId);
    
    // Bileşen sorguları
    List<MaterialCard> getDirectComponents(Long parentMaterialId);
    
    List<MaterialCard> getAllComponents(Long parentMaterialId);
    
    List<MaterialCard> getWhereUsed(Long childMaterialId);
    
    // Hesaplamalar
    Map<String, BigDecimal> calculateMaterialRequirements(Long parentMaterialId, BigDecimal quantity);
    
    Map<String, BigDecimal> calculateCostedBOM(Long parentMaterialId, BigDecimal quantity);
    
    BigDecimal calculateTotalCost(Long parentMaterialId, BigDecimal quantity);
    
    BigDecimal calculateTotalTime(Long parentMaterialId, BigDecimal quantity);
    
    // Doğrulama
    boolean validateBOMStructure(Long parentMaterialId);
    
    boolean hasCircularReference(Long parentMaterialId);
    
    List<String> getCircularReferences(Long parentMaterialId);
    
    boolean isBOMComplete(Long parentMaterialId);
    
    List<String> getMissingComponents(Long parentMaterialId);
    
    // BOM karşılaştırma
    List<BillOfMaterial> compareBOMs(Long bom1Id, Long bom2Id);
    
    Map<String, Object> getBOMDifferences(Long bom1Id, Long bom2Id);
    
    // BOM versiyonlama
    BillOfMaterial createBOMVersion(Long originalBOMId, String version);
    
    List<BillOfMaterial> getBOMVersions(Long parentMaterialId);
    
    BillOfMaterial getActiveBOMVersion(Long parentMaterialId);
    
    void activateBOMVersion(Long bomId);
    
    // BOM kopyalama
    BillOfMaterial copyBOM(Long sourceBOMId, MaterialCard newParentMaterial);
    
    BillOfMaterial copyBOMStructure(Long sourceParentId, MaterialCard newParentMaterial);
    
    // BOM güncelleme
    void updateBOMQuantities(Long parentMaterialId, Map<Long, BigDecimal> quantityUpdates);
    
    void updateBOMCosts(Long parentMaterialId, Map<Long, BigDecimal> costUpdates);
    
    void deactivateBOMs(Long parentMaterialId);
    
    void activateBOMs(Long parentMaterialId);
    
    // Raporlar
    List<Map<String, Object>> getBOMReport(Long parentMaterialId);
    
    List<Map<String, Object>> getCostedBOMReport(Long parentMaterialId);
    
    List<Map<String, Object>> getWhereUsedReport(Long childMaterialId);
    
    List<Map<String, Object>> getBOMComparisonReport(Long bom1Id, Long bom2Id);
    
    // İstatistikler
    long getBOMCount();
    
    long getBOMCountByType(BillOfMaterial.BomType bomType);
    
    long getBOMCountByParent(Long parentMaterialId);
    
    long getBOMCountByChild(Long childMaterialId);
    
    // Doğrulama
    boolean validateBOMLevels(Long parentMaterialId);
    
    boolean validateBOMQuantities(Long parentMaterialId);
    
    boolean validateBOMDates(Long parentMaterialId);
    
    List<String> getBOMValidationErrors(Long parentMaterialId);
    
    // Arama
    List<BillOfMaterial> searchBOMs(String searchTerm);
    
    List<BillOfMaterial> getBOMsByOperation(String operationName);
    
    List<BillOfMaterial> getBOMsByWorkCenter(String workCenter);
    
    // Import/Export
    List<BillOfMaterial> importBOMFromExcel(String filePath);
    
    void exportBOMToExcel(Long parentMaterialId, String filePath);
    
    // Maliyet güncelleme
    void updateBOMCosts(Long parentMaterialId);
    
    void recalculateAllBOMCosts();
    
    // Maliyet analizi
    Map<String, BigDecimal> getBOMCostBreakdown(Long parentMaterialId);
    
    // MRP hesaplamaları
    Map<String, BigDecimal> calculateMRPRequirements(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate);
    
    List<Map<String, Object>> getMRPReport(Long parentMaterialId, BigDecimal quantity, LocalDateTime dueDate);
}