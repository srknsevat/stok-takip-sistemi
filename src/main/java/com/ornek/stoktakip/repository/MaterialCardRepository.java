package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialCardRepository extends JpaRepository<MaterialCard, Long> {
    
    // Temel sorgular
    Optional<MaterialCard> findByMaterialCode(String materialCode);
    
    List<MaterialCard> findByMaterialNameContainingIgnoreCase(String materialName);
    
    List<MaterialCard> findByIsActiveTrue();
    
    List<MaterialCard> findByIsObsoleteTrue();
    
    // Malzeme türüne göre
    List<MaterialCard> findByMaterialType(MaterialCard.MaterialType materialType);
    
    List<MaterialCard> findByMaterialCategory(MaterialCard.MaterialCategory materialCategory);
    
    // Stok sorguları
    List<MaterialCard> findByCurrentStockLessThanEqual(BigDecimal maxStock);
    
    List<MaterialCard> findByCurrentStockLessThan(BigDecimal minStock);
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock <= m.minStockLevel")
    List<MaterialCard> findLowStockMaterials();
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock <= m.reorderPoint")
    List<MaterialCard> findMaterialsNeedingReorder();
    
    @Query("SELECT m FROM MaterialCard m WHERE m.currentStock >= m.maxStockLevel")
    List<MaterialCard> findHighStockMaterials();
    
    // Tedarikçi sorguları
    List<MaterialCard> findBySupplierCode(String supplierCode);
    
    List<MaterialCard> findBySupplierNameContainingIgnoreCase(String supplierName);
    
    // Depolama sorguları
    List<MaterialCard> findByStorageLocation(String storageLocation);
    
    List<MaterialCard> findByHazardousMaterialTrue();
    
    List<MaterialCard> findByBatchControlledTrue();
    
    List<MaterialCard> findBySerialControlledTrue();
    
    // Arama sorguları
    @Query("SELECT m FROM MaterialCard m WHERE " +
           "LOWER(m.materialCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.materialName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<MaterialCard> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // İstatistik sorguları
    @Query("SELECT COUNT(m) FROM MaterialCard m")
    long countTotalMaterials();
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.isActive = true")
    long countActiveMaterials();
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.materialType = :materialType")
    long countByMaterialType(@Param("materialType") MaterialCard.MaterialType materialType);
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.materialCategory = :materialCategory")
    long countByMaterialCategory(@Param("materialCategory") MaterialCard.MaterialCategory materialCategory);
    
    @Query("SELECT COUNT(m) FROM MaterialCard m WHERE m.currentStock <= m.minStockLevel")
    long countLowStockMaterials();
    
    // Maliyet sorguları
    @Query("SELECT SUM(m.currentStock * m.averageCost) FROM MaterialCard m")
    BigDecimal calculateTotalStockValue();
    
    @Query("SELECT SUM(m.currentStock * m.averageCost) FROM MaterialCard m WHERE m.materialType = :materialType")
    BigDecimal calculateStockValueByType(@Param("materialType") MaterialCard.MaterialType materialType);
    
    // BOM sorguları
    @Query("SELECT COUNT(b) FROM BillOfMaterial b WHERE b.parentMaterial.id = :materialId")
    long countBOMsByParent(@Param("materialId") Long materialId);
    
    @Query("SELECT COUNT(b) FROM BillOfMaterial b WHERE b.childMaterial.id = :materialId")
    long countBOMsByChild(@Param("materialId") Long materialId);
    
    // Kalite sorguları
    List<MaterialCard> findByQualityGrade(String qualityGrade);
    
    List<MaterialCard> findByCertificationRequiredTrue();
    
    List<MaterialCard> findByInspectionRequiredTrue();
}
