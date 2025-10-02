package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Category;
import com.ornek.stoktakip.entity.Product;
import com.ornek.stoktakip.repository.CategoryRepository;
import com.ornek.stoktakip.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final Map<String, String> CATEGORY_COLORS = new HashMap<>();
    static {
        CATEGORY_COLORS.put("Elektronik", "#36A2EB");
        CATEGORY_COLORS.put("Bilgisayar Parçaları", "#4BC0C0");
        CATEGORY_COLORS.put("Aksesuar", "#FF6384");
        CATEGORY_COLORS.put("Kamera & Fotoğraf", "#9966FF");
        CATEGORY_COLORS.put("Yazıcı & Tarayıcı", "#FFCE56");
    }

    private static final Map<String, String> CATEGORY_ICONS = new HashMap<>();
    static {
        CATEGORY_ICONS.put("Elektronik", "bi-laptop");
        CATEGORY_ICONS.put("Bilgisayar Parçaları", "bi-cpu");
        CATEGORY_ICONS.put("Aksesuar", "bi-mouse");
        CATEGORY_ICONS.put("Kamera & Fotoğraf", "bi-camera");
        CATEGORY_ICONS.put("Yazıcı & Tarayıcı", "bi-printer");
    }

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeCategories() {
        try {
            if (categoryRepository.count() == 0) {
                Set<String> uniqueCategories = new HashSet<>();
                
                for (Product product : productRepository.findAll()) {
                    if (product.getCategories() != null) {
                        String[] categories = product.getCategories().split(",");
                        for (String category : categories) {
                            uniqueCategories.add(category.trim());
                        }
                    }
                }

                for (String categoryName : uniqueCategories) {
                    Category category = new Category();
                    category.setName(categoryName);
                    category.setColor(CATEGORY_COLORS.getOrDefault(categoryName, "#6c757d"));
                    category.setIcon(CATEGORY_ICONS.getOrDefault(categoryName, "bi-tag"));
                    categoryRepository.save(category);
                }
            }
        } catch (Exception e) {
            // Veritabanı henüz hazır değilse sessizce devam et
        }
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(String categoryName) {
        // Önce bu kategoriye ait ürünleri bul
        List<Product> products = productRepository.findByCategoriesContaining(categoryName);
        
        // Ürünleri sil
        for (Product product : products) {
            productRepository.delete(product);
        }
        
        // Kategoriyi sil
        categoryRepository.deleteByName(categoryName);
    }

    // Diğer metodlar buraya eklenebilir
} 