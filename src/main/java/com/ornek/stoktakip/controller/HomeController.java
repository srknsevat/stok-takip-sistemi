package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    private final ProductService productService;
    private final PlatformService platformService;
    
    @Autowired
    public HomeController(ProductService productService, PlatformService platformService) {
        this.productService = productService;
        this.platformService = platformService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        // İstatistikleri model'e ekle
        model.addAttribute("totalProducts", productService.getTotalProducts());
        model.addAttribute("totalStock", productService.getTotalStock());
        model.addAttribute("totalValue", productService.getTotalValue());
        model.addAttribute("platforms", platformService.getAllPlatforms());
        
        return "index";
    }
}