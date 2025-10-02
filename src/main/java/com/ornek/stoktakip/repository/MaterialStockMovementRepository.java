package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.MaterialStockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaterialStockMovementRepository extends JpaRepository<MaterialStockMovement, Long> {
    
    // Temel sorgular
    List<MaterialStockMovement> findByMaterial(MaterialCard material);
    
    List<MaterialStockMovement> findByMaterialId(Long materialId);
    
    List<MaterialStockMovement> findByMovementType(MaterialStockMovement.MovementType movementType);
    
    List<MaterialStockMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Referans sorguları
    List<MaterialStockMovement> findByReferenceNumber(String referenceNumber);
    
    List<MaterialStockMovement> findByReferenceType(String referenceType);
    
    List<MaterialStockMovement> findByReferenceNumberAndReferenceType(String referenceNumber, String referenceType);
    
    // Batch ve Serial sorguları
    List<MaterialStockMovement> findByBatchNumber(String batchNumber);
    
    List<MaterialStockMovement> findBySerialNumber(String serialNumber);
    
    List<MaterialStockMovement> findByMaterialIdAndBatchNumber(Long materialId, String batchNumber);
    
    List<MaterialStockMovement> findByMaterialIdAndSerialNumber(Long materialId, String serialNumber);
    
    // Lokasyon sorguları
    List<MaterialStockMovement> findByLocation(String location);
    
    List<MaterialStockMovement> findByMaterialIdAndLocation(Long materialId, String location);
    
    // Tarih sorguları
    List<MaterialStockMovement> findByMovementDateAfter(LocalDateTime date);
    
    List<MaterialStockMovement> findByMovementDateBefore(LocalDateTime date);
    
    List<MaterialStockMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Kullanıcı sorguları
    List<MaterialStockMovement> findByCreatedBy(Long createdBy);
    
    // Arama sorguları
    @Query("SELECT m FROM MaterialStockMovement m WHERE " +
           "LOWER(m.material.materialCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.material.materialName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.referenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<MaterialStockMovement> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // İstatistik sorguları
    @Query("SELECT COUNT(m) FROM MaterialStockMovement m WHERE m.material.id = :materialId")
    long countByMaterial(@Param("materialId") Long materialId);
    
    @Query("SELECT COUNT(m) FROM MaterialStockMovement m WHERE m.movementType = :movementType")
    long countByMovementType(@Param("movementType") MaterialStockMovement.MovementType movementType);
    
    @Query("SELECT COUNT(m) FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType = :movementType")
    long countByMaterialAndMovementType(@Param("materialId") Long materialId, 
                                       @Param("movementType") MaterialStockMovement.MovementType movementType);
    
    // Toplam miktar sorguları
    @Query("SELECT SUM(m.quantity) FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType = :movementType")
    Double sumQuantityByMaterialAndMovementType(@Param("materialId") Long materialId, 
                                               @Param("movementType") MaterialStockMovement.MovementType movementType);
    
    @Query("SELECT SUM(m.totalCost) FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType = :movementType")
    Double sumTotalCostByMaterialAndMovementType(@Param("materialId") Long materialId, 
                                                @Param("movementType") MaterialStockMovement.MovementType movementType);
    
    // Giriş/Çıkış sorguları
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType IN ('IN', 'PRODUCTION_IN', 'PURCHASE', 'RETURN')")
    List<MaterialStockMovement> findInboundMovements(@Param("materialId") Long materialId);
    
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType IN ('OUT', 'PRODUCTION_OUT', 'SALES', 'SCRAP')")
    List<MaterialStockMovement> findOutboundMovements(@Param("materialId") Long materialId);
    
    // Son hareketler
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "ORDER BY m.movementDate DESC")
    List<MaterialStockMovement> findLatestMovementsByMaterial(@Param("materialId") Long materialId);
    
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "ORDER BY m.movementDate DESC LIMIT 1")
    MaterialStockMovement findLatestMovementByMaterial(@Param("materialId") Long materialId);
    
    // Tarih aralığı sorguları
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.movementDate DESC")
    List<MaterialStockMovement> findMovementsByMaterialAndDateRange(@Param("materialId") Long materialId,
                                                                    @Param("startDate") LocalDateTime startDate,
                                                                    @Param("endDate") LocalDateTime endDate);
    
    // Rapor sorguları
    @Query("SELECT m.movementType, COUNT(m), SUM(m.quantity), SUM(m.totalCost) " +
           "FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.movementType")
    List<Object[]> findMovementSummaryByMaterialAndDateRange(@Param("materialId") Long materialId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
    
    // Batch kontrolü
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.batchNumber = :batchNumber " +
           "ORDER BY m.movementDate DESC")
    List<MaterialStockMovement> findMovementsByMaterialAndBatch(@Param("materialId") Long materialId,
                                                               @Param("batchNumber") String batchNumber);
    
    // Serial kontrolü
    @Query("SELECT m FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.serialNumber = :serialNumber " +
           "ORDER BY m.movementDate DESC")
    List<MaterialStockMovement> findMovementsByMaterialAndSerial(@Param("materialId") Long materialId,
                                                                @Param("serialNumber") String serialNumber);
    
    // Expiry date sorguları
    List<MaterialStockMovement> findByExpiryDateBefore(LocalDateTime date);
    
    List<MaterialStockMovement> findByExpiryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Maliyet sorguları
    @Query("SELECT AVG(m.unitCost) FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType = 'PURCHASE'")
    Double findAveragePurchaseCost(@Param("materialId") Long materialId);
    
    @Query("SELECT m.unitCost FROM MaterialStockMovement m WHERE m.material.id = :materialId " +
           "AND m.movementType = 'PURCHASE' " +
           "ORDER BY m.movementDate DESC LIMIT 1")
    Double findLastPurchaseCost(@Param("materialId") Long materialId);
}
