package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.service.MaterialCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialCardServiceImpl implements MaterialCardService {
    
    private final MaterialCardRepository materialCardRepository;
    
    @Autowired
    public MaterialCardServiceImpl(MaterialCardRepository materialCardRepository) {
        this.materialCardRepository = materialCardRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getAllMaterialCards() {
        return materialCardRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MaterialCard> getMaterialCardById(Long id) {
        return materialCardRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MaterialCard> getMaterialCardByCode(String materialCode) {
        return materialCardRepository.findByMaterialCode(materialCode);
    }
    
    @Override
    public MaterialCard saveMaterialCard(MaterialCard materialCard) {
        if (materialCard.getId() == null) {
            materialCard.setCreatedAt(LocalDateTime.now());
        }
        materialCard.setUpdatedAt(LocalDateTime.now());
        return materialCardRepository.save(materialCard);
    }
    
    @Override
    public MaterialCard updateMaterialCard(MaterialCard materialCard) {
        MaterialCard existingMaterial = materialCardRepository.findById(materialCard.getId())
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialCard.getId()));
        
        materialCard.setUpdatedAt(LocalDateTime.now());
        return materialCardRepository.save(materialCard);
    }
    
    @Override
    public void deleteMaterialCard(Long id) {
        materialCardRepository.deleteById(id);
    }
    
    @Override
    public MaterialCard createMaterialCard(String materialCode, String materialName, 
                                         MaterialCard.MaterialType materialType, 
                                         MaterialCard.MaterialCategory materialCategory) {
        if (isMaterialCodeExists(materialCode)) {
            throw new RuntimeException("Malzeme kodu zaten kullanımda: " + materialCode);
        }
        
        MaterialCard materialCard = new MaterialCard(materialCode, materialName, materialType, materialCategory);
        materialCard.setCreatedAt(LocalDateTime.now());
        materialCard.setUpdatedAt(LocalDateTime.now());
        
        return materialCardRepository.save(materialCard);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> searchMaterialCards(String searchTerm) {
        return materialCardRepository.findBySearchTerm(searchTerm);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialCardsByType(MaterialCard.MaterialType materialType) {
        return materialCardRepository.findByMaterialType(materialType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialCardsByCategory(MaterialCard.MaterialCategory materialCategory) {
        return materialCardRepository.findByMaterialCategory(materialCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getActiveMaterialCards() {
        return materialCardRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getObsoleteMaterialCards() {
        return materialCardRepository.findByIsObsoleteTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getLowStockMaterials() {
        return materialCardRepository.findLowStockMaterials();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsNeedingReorder() {
        return materialCardRepository.findMaterialsNeedingReorder();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getHighStockMaterials() {
        return materialCardRepository.findHighStockMaterials();
    }
    
    @Override
    public void updateStock(Long materialId, BigDecimal quantity, String reason) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        BigDecimal newStock = material.getCurrentStock().add(quantity);
        material.setCurrentStock(newStock);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void adjustStock(Long materialId, BigDecimal newQuantity, String reason) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setCurrentStock(newQuantity);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateStandardCost(Long materialId, BigDecimal newCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setStandardCost(newCost);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateAverageCost(Long materialId, BigDecimal newCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setAverageCost(newCost);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateLastPurchaseCost(Long materialId, BigDecimal newCost) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setLastPurchaseCost(newCost);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalStockValue() {
        return materialCardRepository.calculateTotalStockValue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateStockValueByType(MaterialCard.MaterialType materialType) {
        return materialCardRepository.calculateStockValueByType(materialType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalMaterialCount() {
        return materialCardRepository.countTotalMaterials();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getMaterialCountByType(MaterialCard.MaterialType materialType) {
        return materialCardRepository.countByMaterialType(materialType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getMaterialCountByCategory(MaterialCard.MaterialCategory materialCategory) {
        return materialCardRepository.countByMaterialCategory(materialCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getActiveMaterialCount() {
        return materialCardRepository.countActiveMaterials();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getLowStockMaterialCount() {
        return materialCardRepository.countLowStockMaterials();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMaterialCodeExists(String materialCode) {
        return materialCardRepository.findByMaterialCode(materialCode).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMaterialCodeExists(String materialCode, Long excludeId) {
        Optional<MaterialCard> existing = materialCardRepository.findByMaterialCode(materialCode);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMaterialNameExists(String materialName) {
        return !materialCardRepository.findByMaterialNameContainingIgnoreCase(materialName).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMaterialNameExists(String materialName, Long excludeId) {
        List<MaterialCard> existing = materialCardRepository.findByMaterialNameContainingIgnoreCase(materialName);
        return existing.stream().anyMatch(m -> !m.getId().equals(excludeId));
    }
    
    @Override
    public void markAsObsolete(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setIsObsolete(true);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void reactivateMaterial(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setIsObsolete(false);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateSupplierInfo(Long materialId, String supplierCode, String supplierName) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setSupplierCode(supplierCode);
        material.setSupplierName(supplierName);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    public void updateStorageInfo(Long materialId, String storageLocation, String storageConditions) {
        MaterialCard material = materialCardRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Malzeme bulunamadı: " + materialId));
        
        material.setStorageLocation(storageLocation);
        material.setStorageConditions(storageConditions);
        material.setUpdatedAt(LocalDateTime.now());
        
        materialCardRepository.save(material);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsUsedInBOM(Long parentMaterialId) {
        // Bu kısım BOM repository'sinden gelecek
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsThatUseThis(Long childMaterialId) {
        // Bu kısım BOM repository'sinden gelecek
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasBOM(Long materialId) {
        return materialCardRepository.countBOMsByParent(materialId) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUsedInBOM(Long materialId) {
        return materialCardRepository.countBOMsByChild(materialId) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsBySupplier(String supplierCode) {
        return materialCardRepository.findBySupplierCode(supplierCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsByStorageLocation(String storageLocation) {
        return materialCardRepository.findByStorageLocation(storageLocation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getMaterialsByQualityGrade(String qualityGrade) {
        return materialCardRepository.findByQualityGrade(qualityGrade);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getHazardousMaterials() {
        return materialCardRepository.findByHazardousMaterialTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getBatchControlledMaterials() {
        return materialCardRepository.findByBatchControlledTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialCard> getSerialControlledMaterials() {
        return materialCardRepository.findBySerialControlledTrue();
    }
}
