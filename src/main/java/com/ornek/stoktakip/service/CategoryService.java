package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    List<Category> getAllCategories();
    
    Optional<Category> getCategoryById(Long id);
    
    Category saveCategory(Category category);
    
    void deleteCategory(Long id);
    
    List<Category> findByNameContaining(String name);
    
    List<Category> findActiveCategories();
    
    long countCategories();
}