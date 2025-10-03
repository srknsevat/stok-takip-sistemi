package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.Category;
import com.ornek.stoktakip.repository.CategoryRepository;
import com.ornek.stoktakip.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public List<Category> findByNameContaining(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Category> findActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    @Override
    public long countCategories() {
        return categoryRepository.count();
    }
}
