package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.entity.OrderItem;
import com.ornek.stoktakip.entity.MaterialCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByProduct(Product product);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId ORDER BY oi.createdAt ASC")
    List<OrderItem> findByOrderIdOrderByCreatedAt(@Param("orderId") Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId ORDER BY oi.createdAt DESC")
    List<OrderItem> findByProductIdOrderByCreatedAt(@Param("productId") Long productId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.status = :status")
    List<OrderItem> findByOrderStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderDate BETWEEN :startDate AND :endDate")
    List<OrderItem> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.platform.id = :platformId")
    List<OrderItem> findByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.platform.id = :platformId AND oi.order.status = :status")
    List<OrderItem> findByPlatformIdAndStatus(@Param("platformId") Long platformId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    BigDecimal sumQuantityByProductId(@Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId")
    BigDecimal sumTotalPriceByProductId(@Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.platform.id = :platformId")
    BigDecimal sumQuantityByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.platform.id = :platformId")
    BigDecimal sumTotalPriceByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT oi.product, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();
    
    @Query("SELECT oi.order.platform, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.order.platform ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingPlatforms();
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.platform.id = :platformId")
    Long countByPlatformId(@Param("platformId") Long platformId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity >= :minQuantity")
    List<OrderItem> findByQuantityGreaterThanEqual(@Param("minQuantity") BigDecimal minQuantity);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.unitPrice >= :minPrice")
    List<OrderItem> findByUnitPriceGreaterThanEqual(@Param("minPrice") BigDecimal minPrice);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.totalPrice >= :minTotalPrice")
    List<OrderItem> findByTotalPriceGreaterThanEqual(@Param("minTotalPrice") BigDecimal minTotalPrice);
}