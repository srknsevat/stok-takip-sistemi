package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.ProductService;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    private final ProductService productService;
    private final PlatformService platformService;
    private final UserService userService;
    
    @Autowired
    public HealthController(ProductService productService, 
                          PlatformService platformService, 
                          UserService userService) {
        this.productService = productService;
        this.platformService = platformService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Sistem durumu
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            
            // Veritabanı durumu
            Map<String, Object> database = new HashMap<>();
            database.put("status", "UP");
            database.put("totalProducts", productService.getTotalProducts());
            database.put("totalUsers", userService.getTotalUserCount());
            database.put("totalPlatforms", platformService.countActivePlatforms());
            health.put("database", database);
            
            // Platform durumu
            Map<String, Object> platforms = new HashMap<>();
            platforms.put("total", platformService.getAllPlatforms().size());
            platforms.put("active", platformService.getActivePlatforms().size());
            platforms.put("syncEnabled", platformService.getSyncEnabledPlatforms().size());
            health.put("platforms", platforms);
            
            // Sistem bilgileri
            Map<String, Object> system = new HashMap<>();
            system.put("javaVersion", System.getProperty("java.version"));
            system.put("osName", System.getProperty("os.name"));
            system.put("osVersion", System.getProperty("os.version"));
            system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
            system.put("freeMemory", Runtime.getRuntime().freeMemory());
            system.put("totalMemory", Runtime.getRuntime().totalMemory());
            system.put("maxMemory", Runtime.getRuntime().maxMemory());
            health.put("system", system);
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("timestamp", LocalDateTime.now());
            health.put("error", e.getMessage());
            return ResponseEntity.status(500).body(health);
        }
    }
    
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now());
            
            // Detaylı sistem durumu
            Map<String, Object> details = new HashMap<>();
            
            // Ürün istatistikleri
            Map<String, Object> products = new HashMap<>();
            products.put("total", productService.getTotalProducts());
            products.put("totalStock", productService.getTotalStock());
            products.put("totalValue", productService.getTotalValue());
            details.put("products", products);
            
            // Kullanıcı istatistikleri
            Map<String, Object> users = new HashMap<>();
            users.put("total", userService.getTotalUserCount());
            users.put("active", userService.getActiveUserCount());
            users.put("locked", userService.getLockedUsers().size());
            details.put("users", users);
            
            // Platform istatistikleri
            Map<String, Object> platforms = new HashMap<>();
            platforms.put("total", platformService.getAllPlatforms().size());
            platforms.put("active", platformService.getActivePlatforms().size());
            platforms.put("syncEnabled", platformService.getSyncEnabledPlatforms().size());
            details.put("platforms", platforms);
            
            health.put("details", details);
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("timestamp", LocalDateTime.now());
            health.put("error", e.getMessage());
            return ResponseEntity.status(500).body(health);
        }
    }
}
