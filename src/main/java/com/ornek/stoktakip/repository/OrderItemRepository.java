package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.OrderItem;
import com.ornek.stoktakip.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByProduct(Product product);
    
    List<OrderItem> findByOrderAndIsProcessedFalse(Order order);
    
    List<OrderItem> findByProductAndIsProcessedFalse(Product product);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order = :order AND oi.isProcessed = false")
    List<OrderItem> findUnprocessedByOrder(@Param("order") Order order);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product = :product AND oi.isProcessed = false")
    List<OrderItem> findUnprocessedByProduct(@Param("product") Product product);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product = :product AND oi.isProcessed = false")
    Integer sumUnprocessedQuantityByProduct(@Param("product") Product product);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.platform.id = :platformId AND oi.isProcessed = false")
    List<OrderItem> findUnprocessedByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderStatus IN ('PENDING', 'CONFIRMED', 'PROCESSING') AND oi.isProcessed = false")
    List<OrderItem> findUnprocessedActiveOrderItems();
}
