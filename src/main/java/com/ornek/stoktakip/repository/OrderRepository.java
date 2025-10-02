package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByPlatformId(Long platformId);
    
    List<Order> findByProductId(Long productId);
    
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    
    List<Order> findByOrderType(Order.OrderType orderType);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByPlatformAndOrderDateBetween(Platform platform, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByPlatformIdAndOrderDateBetween(Long platformId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByOrderNumberContainingIgnoreCase(String orderNumber);
    
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Order> findByCustomerEmailContainingIgnoreCase(String customerEmail);
    
    List<Order> findByPlatformOrderId(String platformOrderId);
    
    @Query("SELECT o FROM Order o WHERE o.platform.id = :platformId AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    List<Order> findByPlatformAndDateRange(@Param("platformId") Long platformId, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.platform.id = :platformId AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Long countByPlatformAndDateRange(@Param("platformId") Long platformId, 
                                    @Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.platform = :platform AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Long countByPlatformAndOrderDateBetween(@Param("platform") Platform platform, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.platform.id = :platformId ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByPlatform(@Param("platformId") Long platformId);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = :status AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    List<Order> findByStatusAndDateRange(@Param("status") Order.OrderStatus status, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByOrderStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderType = :type")
    Long countByOrderType(@Param("type") Order.OrderType type);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.platform.id = :platformId")
    Long countByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    // Status için alias metodlar
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);
}