package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Category;
import com.ornek.stoktakip.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    private final MessageSource messageSource;

    @Autowired
    public CategoryController(CategoryService categoryService, MessageSource messageSource) {
        this.categoryService = categoryService;
        this.messageSource = messageSource;
    }

    @PostMapping("/add")
    public String addCategory(@RequestParam String name,
                            @RequestParam String color,
                            RedirectAttributes redirectAttributes,
                            Locale locale) {
        try {
            Category category = new Category();
            category.setName(name);
            category.setColor(color);
            category.setIcon("bi-tag"); // Varsayılan icon
            
            categoryService.saveCategory(category);
            String message = messageSource.getMessage("category.added.success", 
                new Object[]{name}, locale);
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam String name, 
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        try {
            categoryService.deleteCategory(name);
            String message = messageSource.getMessage("category.deleted.success", 
                new Object[]{name}, locale);
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }
} 