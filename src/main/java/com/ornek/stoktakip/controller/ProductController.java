package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Product;
import com.ornek.stoktakip.entity.StockMovement;
import com.ornek.stoktakip.service.CategoryService;
import com.ornek.stoktakip.service.ProductService;
import com.ornek.stoktakip.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StockMovementService stockMovementService;
    private final MessageSource messageSource;

    @Autowired
    public ProductController(ProductService productService, 
                           CategoryService categoryService,
                           StockMovementService stockMovementService,
                           MessageSource messageSource) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.stockMovementService = stockMovementService;
        this.messageSource = messageSource;
    }

    // Ürün listesi sayfası
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    // Yeni ürün ekleme formu
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCreationDate(LocalDate.now());
        product.setStockQuantity(0);
        product.setPrice(BigDecimal.ZERO);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/form";
    }

    // Ürün kaydetme
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute Product product, 
                            BindingResult result, 
                            RedirectAttributes redirectAttributes,
                            Locale locale) {
        try {
            if (result.hasErrors()) {
                return "products/form";
            }
            
            productService.saveProduct(product);
            String message = messageSource.getMessage("product.updated.success", null, locale);
            redirectAttributes.addFlashAttribute("message", message);
            
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "products/form";
        }
    }

    // Ürün düzenleme formu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(product -> {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories());
        });
        return "products/form";
    }

    // Ürün detay sayfası
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        productService.getProductById(id)
                .ifPresent(product -> model.addAttribute("product", product));
        return "products/detail";
    }

    // Ürün silme
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes,
                              Locale locale) {
        productService.deleteProduct(id);
        String message = messageSource.getMessage("product.deleted.success", null, locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/products";
    }

    // Stok güncelleme sayfası
    @GetMapping("/stock/{id}")
    public String showStockForm(@PathVariable Long id, Model model) {
        productService.getProductById(id)
                .ifPresent(product -> model.addAttribute("product", product));
        return "products/stock";
    }

    // Stok güncelleme işlemi
    @PostMapping("/stock/{id}")
    public String updateStock(@PathVariable Long id, 
                            @RequestParam Integer quantity, 
                            RedirectAttributes redirectAttributes) {
        try {
            productService.updateStock(id, quantity);
            redirectAttributes.addFlashAttribute("message", "Stok başarıyla güncellendi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/stock/add/{id}")
    public String showAddStockForm(@PathVariable Long id, Model model) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("stockOperation", "add");
                    return "products/stock";
                })
                .orElse("redirect:/products");
    }

    @GetMapping("/stock/remove/{id}")
    public String showRemoveStockForm(@PathVariable Long id, Model model) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("stockOperation", "remove");
                    return "products/stock";
                })
                .orElse("redirect:/products");
    }

    @PostMapping("/stock/{operation}/{id}")
    public String updateStock(@PathVariable Long id, 
                             @PathVariable String operation,
                             @RequestParam Integer quantity, 
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        try {
            return productService.getProductById(id)
                    .map(product -> {
                        // Stok hareketi oluştur
                        StockMovement movement = new StockMovement();
                        movement.setProduct(product);
                        movement.setMovementDate(LocalDateTime.now());

                        if ("add".equals(operation)) {
                            if (quantity <= 0) {
                                throw new IllegalArgumentException("Eklenen stok miktarı pozitif olmalıdır");
                            }
                            movement.setQuantity(quantity);
                            movement.setMovementType("ENTRY");
                            product.setStockQuantity(product.getStockQuantity() + quantity);
                        } else if ("remove".equals(operation)) {
                            if (quantity <= 0) {
                                throw new IllegalArgumentException("Silinen stok miktarı pozitif olmalıdır");
                            }
                            if (product.getStockQuantity() < quantity) {
                                throw new IllegalArgumentException("Yeterli stok bulunmamaktadır");
                            }
                            movement.setQuantity(-quantity); // Eksi işareti ile çıkışı belirt
                            movement.setMovementType("EXIT");
                            product.setStockQuantity(product.getStockQuantity() - quantity);
                        }

                        // Önce ürünü kaydet
                        productService.saveProduct(product);
                        // Sonra stok hareketini kaydet
                        stockMovementService.saveMovement(movement);

                        // Mesajı çeviri ile göster
                        String messageKey = "add".equals(operation) ? "stock.added.success" : "stock.removed.success";
                        String message = messageSource.getMessage(messageKey, null, locale);
                        redirectAttributes.addFlashAttribute("message", message);
                        
                        return "redirect:/products";
                    })
                    .orElse("redirect:/products");
                
        } catch (IllegalArgumentException e) {
            String errorKey = "error.quantity." + (e.getMessage().contains("pozitif") ? "positive" : "insufficient");
            String errorMessage = messageSource.getMessage(errorKey, null, locale);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/products";
        }
    }

    @GetMapping("/category/{categoryName}")
    public String listProductsByCategory(@PathVariable String categoryName, Model model) {
        List<Product> products = productService.findByCategory(categoryName);
        model.addAttribute("products", products);
        model.addAttribute("categoryName", categoryName);
        return "products/list";
    }
} 