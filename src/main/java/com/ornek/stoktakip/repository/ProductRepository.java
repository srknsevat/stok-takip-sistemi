package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Ürün adına göre arama
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Fiyat aralığına göre arama
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Stok miktarı belirli bir değerden az olan ürünleri bulma
    List<Product> findByStockQuantityLessThan(Integer stockQuantity);
    
    // Belirli bir kategoriye sahip ürünleri bulma
    List<Product> findByCategoriesContainingIgnoreCase(String category);
    
    List<Product> findByCategoriesContaining(String category);

    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    long sumStockQuantity();
    
    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p")
    BigDecimal sumTotalValue();
} 