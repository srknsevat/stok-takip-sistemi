package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    
    // Temel sorgular
    List<BillOfMaterial> findByParentMaterial(MaterialCard parentMaterial);
    
    List<BillOfMaterial> findByChildMaterial(MaterialCard childMaterial);
    
    List<BillOfMaterial> findByParentMaterialId(Long parentMaterialId);
    
    List<BillOfMaterial> findByChildMaterialId(Long childMaterialId);
    
    List<BillOfMaterial> findByIsActiveTrue();
    
    // BOM türüne göre
    List<BillOfMaterial> findByBomType(BillOfMaterial.BomType bomType);
    
    List<BillOfMaterial> findByParentMaterialIdAndBomType(Long parentMaterialId, BillOfMaterial.BomType bomType);
    
    // BOM seviyesine göre
    List<BillOfMaterial> findByBomLevel(Integer bomLevel);
    
    List<BillOfMaterial> findByParentMaterialIdAndBomLevel(Long parentMaterialId, Integer bomLevel);
    
    // Aktif BOM'lar
    List<BillOfMaterial> findByParentMaterialIdAndIsActiveTrue(Long parentMaterialId);
    
    List<BillOfMaterial> findByChildMaterialIdAndIsActiveTrue(Long childMaterialId);
    
    // Etkili BOM'lar (tarih aralığında)
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "AND b.isActive = true " +
           "AND (b.effectiveFrom IS NULL OR b.effectiveFrom <= CURRENT_TIMESTAMP) " +
           "AND (b.effectiveTo IS NULL OR b.effectiveTo >= CURRENT_TIMESTAMP)")
    List<BillOfMaterial> findEffectiveBOMsByParent(@Param("parentMaterialId") Long parentMaterialId);
    
    // Operasyon sorguları
    List<BillOfMaterial> findByOperationName(String operationName);
    
    List<BillOfMaterial> findByWorkCenter(String workCenter);
    
    // Arama sorguları
    @Query("SELECT b FROM BillOfMaterial b WHERE " +
           "LOWER(b.parentMaterial.materialCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.parentMaterial.materialName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.childMaterial.materialCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.childMaterial.materialName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.operationName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<BillOfMaterial> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // İstatistik sorguları
    @Query("SELECT COUNT(b) FROM BillOfMaterial b")
    long countTotalBOMs();
    
    @Query("SELECT COUNT(b) FROM BillOfMaterial b WHERE b.bomType = :bomType")
    long countByBomType(@Param("bomType") BillOfMaterial.BomType bomType);
    
    @Query("SELECT COUNT(b) FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId")
    long countByParent(@Param("parentMaterialId") Long parentMaterialId);
    
    @Query("SELECT COUNT(b) FROM BillOfMaterial b WHERE b.childMaterial.id = :childMaterialId")
    long countByChild(@Param("childMaterialId") Long childMaterialId);
    
    // BOM yapısı sorguları
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "ORDER BY b.bomLevel, b.operationSequence")
    List<BillOfMaterial> findBOMStructureByParent(@Param("parentMaterialId") Long parentMaterialId);
    
    @Query("SELECT b FROM BillOfMaterial b WHERE b.childMaterial.id = :childMaterialId " +
           "ORDER BY b.bomLevel, b.operationSequence")
    List<BillOfMaterial> findWhereUsedByChild(@Param("childMaterialId") Long childMaterialId);
    
    // Dairesel referans kontrolü
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "AND b.childMaterial.id = :childMaterialId")
    List<BillOfMaterial> findCircularReference(@Param("parentMaterialId") Long parentMaterialId, 
                                             @Param("childMaterialId") Long childMaterialId);
    
    // BOM seviyesi kontrolü
    @Query("SELECT MAX(b.bomLevel) FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId")
    Optional<Integer> findMaxBomLevel(@Param("parentMaterialId") Long parentMaterialId);
    
    // Alternatif BOM'lar
    List<BillOfMaterial> findByAlternativeBom(String alternativeBom);
    
    List<BillOfMaterial> findByParentMaterialIdAndAlternativeBom(Long parentMaterialId, String alternativeBom);
    
    // Birincil BOM'lar
    List<BillOfMaterial> findByIsPrimaryTrue();
    
    List<BillOfMaterial> findByParentMaterialIdAndIsPrimaryTrue(Long parentMaterialId);
    
    // Hayalet BOM'lar
    List<BillOfMaterial> findByIsPhantomTrue();
    
    // Maliyet sorguları
    @Query("SELECT SUM(b.totalCost) FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId")
    Optional<Double> calculateTotalCostByParent(@Param("parentMaterialId") Long parentMaterialId);
    
    @Query("SELECT SUM(b.setupTime + b.runTime) FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId")
    Optional<Double> calculateTotalTimeByParent(@Param("parentMaterialId") Long parentMaterialId);
    
    // BOM karşılaştırma
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "AND b.bomType = :bomType " +
           "ORDER BY b.createdAt DESC")
    List<BillOfMaterial> findBOMVersions(@Param("parentMaterialId") Long parentMaterialId, 
                                        @Param("bomType") BillOfMaterial.BomType bomType);
    
    // BOM doğrulama
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "AND b.quantity <= 0")
    List<BillOfMaterial> findInvalidQuantities(@Param("parentMaterialId") Long parentMaterialId);
    
    @Query("SELECT b FROM BillOfMaterial b WHERE b.parentMaterial.id = :parentMaterialId " +
           "AND b.effectiveFrom > b.effectiveTo")
    List<BillOfMaterial> findInvalidDateRanges(@Param("parentMaterialId") Long parentMaterialId);
}
