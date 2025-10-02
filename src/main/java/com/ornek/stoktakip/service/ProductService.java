package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Temel CRUD operasyonları
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    void deleteProduct(Long id);
    
    // Özel arama metodları
    List<Product> searchByName(String name);
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    List<Product> findLowStockProducts(Integer threshold);
    List<Product> findByCategory(String categoryName);
    
    // Özel iş mantığı metodları
    boolean isStockAvailable(Long productId, Integer quantity);
    void updateStock(Long productId, Integer quantity);
    
    // Özet metodları
    long getTotalProducts();
    long getTotalStock();
    BigDecimal getTotalValue();
} 