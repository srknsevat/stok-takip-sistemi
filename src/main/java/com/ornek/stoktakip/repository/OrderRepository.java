package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Optional<Order> findByPlatformOrderIdAndPlatform(String platformOrderId, Platform platform);
    
    List<Order> findByPlatform(Platform platform);
    
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> findByPlatformAndOrderStatus(Platform platform, Order.OrderStatus orderStatus);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.platform = :platform AND o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByPlatformAndOrderDateBetween(@Param("platform") Platform platform, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.platform = :platform AND o.orderStatus = :status")
    long countByPlatformAndStatus(@Param("platform") Platform platform, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.platform = :platform AND o.orderDate BETWEEN :startDate AND :endDate AND o.paymentStatus = 'PAID'")
    Double sumTotalAmountByPlatformAndDateRange(@Param("platform") Platform platform, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('PENDING', 'CONFIRMED', 'PROCESSING')")
    List<Order> findActiveOrders();
    
    @Query("SELECT o FROM Order o WHERE o.platform = :platform AND o.orderStatus IN ('PENDING', 'CONFIRMED', 'PROCESSING')")
    List<Order> findActiveOrdersByPlatform(@Param("platform") Platform platform);
}
