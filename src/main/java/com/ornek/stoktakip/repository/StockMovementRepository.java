package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    void deleteByProductId(Long productId);
    List<StockMovement> findAllByOrderByMovementDateDesc();
} 