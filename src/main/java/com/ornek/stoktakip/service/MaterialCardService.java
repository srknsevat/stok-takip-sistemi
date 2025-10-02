package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.MaterialCard;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MaterialCardService {
    
    // Temel CRUD operasyonları
    List<MaterialCard> getAllMaterialCards();
    
    Optional<MaterialCard> getMaterialCardById(Long id);
    
    Optional<MaterialCard> getMaterialCardByCode(String materialCode);
    
    MaterialCard saveMaterialCard(MaterialCard materialCard);
    
    MaterialCard updateMaterialCard(MaterialCard materialCard);
    
    void deleteMaterialCard(Long id);
    
    // Malzeme oluşturma
    MaterialCard createMaterialCard(String materialCode, String materialName, 
                                  MaterialCard.MaterialType materialType, 
                                  MaterialCard.MaterialCategory materialCategory);
    
    // Arama ve filtreleme
    List<MaterialCard> searchMaterialCards(String searchTerm);
    
    List<MaterialCard> getMaterialCardsByType(MaterialCard.MaterialType materialType);
    
    List<MaterialCard> getMaterialCardsByCategory(MaterialCard.MaterialCategory materialCategory);
    
    List<MaterialCard> getActiveMaterialCards();
    
    List<MaterialCard> getObsoleteMaterialCards();
    
    // Stok yönetimi
    List<MaterialCard> getLowStockMaterials();
    
    List<MaterialCard> getMaterialsNeedingReorder();
    
    List<MaterialCard> getHighStockMaterials();
    
    void updateStock(Long materialId, BigDecimal quantity, String reason);
    
    void adjustStock(Long materialId, BigDecimal newQuantity, String reason);
    
    // Maliyet yönetimi
    void updateStandardCost(Long materialId, BigDecimal newCost);
    
    void updateAverageCost(Long materialId, BigDecimal newCost);
    
    void updateLastPurchaseCost(Long materialId, BigDecimal newCost);
    
    // İstatistikler
    BigDecimal calculateTotalStockValue();
    
    BigDecimal calculateStockValueByType(MaterialCard.MaterialType materialType);
    
    long getTotalMaterialCount();
    
    long getMaterialCountByType(MaterialCard.MaterialType materialType);
    
    long getMaterialCountByCategory(MaterialCard.MaterialCategory materialCategory);
    
    long getActiveMaterialCount();
    
    long getLowStockMaterialCount();
    
    // Dashboard için ek metodlar
    long countAllMaterialCards();
    
    long getTotalStock();
    
    BigDecimal getTotalValue();
    
    // Doğrulama
    boolean isMaterialCodeExists(String materialCode);
    
    boolean isMaterialCodeExists(String materialCode, Long excludeId);
    
    boolean isMaterialNameExists(String materialName);
    
    boolean isMaterialNameExists(String materialName, Long excludeId);
    
    // Durum yönetimi
    void markAsObsolete(Long materialId);
    
    void reactivateMaterial(Long materialId);
    
    // Tedarikçi bilgileri
    void updateSupplierInfo(Long materialId, String supplierCode, String supplierName);
    
    // Depolama bilgileri
    void updateStorageInfo(Long materialId, String storageLocation, String storageConditions);
    
    // BOM ilişkileri
    List<MaterialCard> getMaterialsUsedInBOM(Long parentMaterialId);
    
    List<MaterialCard> getMaterialsThatUseThis(Long childMaterialId);
    
    boolean hasBOM(Long materialId);
    
    boolean isUsedInBOM(Long materialId);
    
    // Tedarikçi sorguları
    List<MaterialCard> getMaterialsBySupplier(String supplierCode);
    
    // Depolama sorguları
    List<MaterialCard> getMaterialsByStorageLocation(String storageLocation);
    
    // Kalite sorguları
    List<MaterialCard> getMaterialsByQualityGrade(String qualityGrade);
    
    // Özel malzemeler
    List<MaterialCard> getHazardousMaterials();
    
    List<MaterialCard> getBatchControlledMaterials();
    
    List<MaterialCard> getSerialControlledMaterials();
}