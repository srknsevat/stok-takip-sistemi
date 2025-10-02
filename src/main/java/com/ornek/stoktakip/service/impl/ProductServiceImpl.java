package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.Product;
import com.ornek.stoktakip.repository.ProductRepository;
import com.ornek.stoktakip.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }
    
    @Override
    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }
    
    @Override
    public List<Product> findByCategory(String categoryName) {
        return productRepository.findByCategoriesContainingIgnoreCase(categoryName);
    }
    
    @Override
    public boolean isStockAvailable(Long productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStockQuantity() >= quantity;
    }
    
    @Override
    public void updateStock(Long productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            p.setStockQuantity(p.getStockQuantity() + quantity);
            productRepository.save(p);
        }
    }
    
    @Override
    public long getTotalProducts() {
        return productRepository.count();
    }
    
    @Override
    public long getTotalStock() {
        return productRepository.sumStockQuantity();
    }
    
    @Override
    public BigDecimal getTotalValue() {
        return productRepository.sumTotalValue();
    }
} 