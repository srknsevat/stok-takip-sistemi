package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.MaterialCardService;
import com.ornek.stoktakip.service.StockSyncService;
import com.ornek.stoktakip.repository.OrderRepository;
import com.ornek.stoktakip.repository.SyncLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private final PlatformService platformService;
    private final MaterialCardService materialCardService;
    private final StockSyncService stockSyncService;
    private final OrderRepository orderRepository;
    private final SyncLogRepository syncLogRepository;
    
    @Autowired
    public DashboardController(PlatformService platformService,
                             MaterialCardService materialCardService,
                             StockSyncService stockSyncService,
                             OrderRepository orderRepository,
                             SyncLogRepository syncLogRepository) {
        this.platformService = platformService;
        this.materialCardService = materialCardService;
        this.stockSyncService = stockSyncService;
        this.orderRepository = orderRepository;
        this.syncLogRepository = syncLogRepository;
    }
    
    /**
     * Ana dashboard sayfası
     */
    @GetMapping
    public String dashboard(Model model) {
        // Genel istatistikler
        Map<String, Object> statistics = getGeneralStatistics();
        model.addAttribute("statistics", statistics);
        
        // Platform istatistikleri
        List<Map<String, Object>> platformStats = getPlatformStatistics();
        model.addAttribute("platformStats", platformStats);
        
        // Son senkronizasyon logları
        List<Map<String, Object>> recentSyncs = getRecentSyncLogs();
        model.addAttribute("recentSyncs", recentSyncs);
        
        // Stok uyarıları
        List<Map<String, Object>> stockAlerts = getStockAlerts();
        model.addAttribute("stockAlerts", stockAlerts);
        
        // Son siparişler
        List<Map<String, Object>> recentOrders = getRecentOrders();
        model.addAttribute("recentOrders", recentOrders);
        
        return "dashboard/index";
    }
    
    /**
     * Platform detay sayfası
     */
    @GetMapping("/platform/{platformId}")
    public String platformDetail(@PathVariable Long platformId, Model model) {
        Platform platform = platformService.getPlatformById(platformId).orElse(null);
        if (platform == null) {
            return "redirect:/dashboard?error=platform_not_found";
        }
        
        model.addAttribute("platform", platform);
        
        // Platform'a özel istatistikler
        Map<String, Object> platformStats = getPlatformDetailStatistics(platformId);
        model.addAttribute("platformStats", platformStats);
        
        // Platform'daki ürünler
        List<Map<String, Object>> platformProducts = getPlatformProducts(platformId);
        model.addAttribute("platformProducts", platformProducts);
        
        // Platform senkronizasyon logları
        List<Map<String, Object>> platformSyncLogs = getPlatformSyncLogs(platformId);
        model.addAttribute("platformSyncLogs", platformSyncLogs);
        
        return "dashboard/platform-detail";
    }
    
    /**
     * Stok raporu sayfası
     */
    @GetMapping("/stock-report")
    public String stockReport(Model model,
                            @RequestParam(required = false) String platformCode,
                            @RequestParam(required = false) String category) {
        
        // Stok raporu verileri
        List<Map<String, Object>> stockReport = getStockReport(platformCode, category);
        model.addAttribute("stockReport", stockReport);
        
        // Platform listesi (filtre için)
        List<Platform> platforms = platformService.getActivePlatforms();
        model.addAttribute("platforms", platforms);
        
        // Kategori listesi (filtre için)
        List<String> categories = getCategories();
        model.addAttribute("categories", categories);
        
        model.addAttribute("selectedPlatform", platformCode);
        model.addAttribute("selectedCategory", category);
        
        return "dashboard/stock-report";
    }
    
    /**
     * Senkronizasyon durumu sayfası
     */
    @GetMapping("/sync-status")
    public String syncStatus(Model model) {
        // Senkronizasyon durumu
        Map<String, Object> syncStatus = getSyncStatus();
        model.addAttribute("syncStatus", syncStatus);
        
        // Başarısız senkronizasyonlar
        List<Map<String, Object>> failedSyncs = getFailedSyncs();
        model.addAttribute("failedSyncs", failedSyncs);
        
        // Platform senkronizasyon durumları
        List<Map<String, Object>> platformSyncStatus = getPlatformSyncStatus();
        model.addAttribute("platformSyncStatus", platformSyncStatus);
        
        return "dashboard/sync-status";
    }
    
    /**
     * Sipariş raporu sayfası
     */
    @GetMapping("/order-report")
    public String orderReport(Model model,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String platformCode) {
        
        // Tarih aralığı belirleme
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now();
        
        // Sipariş raporu verileri
        List<Map<String, Object>> orderReport = getOrderReport(start, end, platformCode);
        model.addAttribute("orderReport", orderReport);
        
        // Platform listesi (filtre için)
        List<Platform> platforms = platformService.getActivePlatforms();
        model.addAttribute("platforms", platforms);
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedPlatform", platformCode);
        
        return "dashboard/order-report";
    }
    
    /**
     * Gerçek zamanlı stok durumu API
     */
    @GetMapping("/api/real-time-stock")
    public ResponseEntity<Map<String, Object>> getRealTimeStock(@RequestParam Long productId) {
        try {
            MaterialCard product = materialCardService.getMaterialCardById(productId).orElse(null);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            Integer realTimeStock = stockSyncService.calculateRealTimeStock(product);
            boolean isSynced = stockSyncService.isStockSynced(product);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productId", productId);
            response.put("productName", product.getMaterialName());
            response.put("currentStock", product.getCurrentStock());
            response.put("realTimeStock", realTimeStock);
            response.put("isSynced", isSynced);
            response.put("lastSyncAt", product.getUpdatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Platform senkronizasyonu başlatma API
     */
    @PostMapping("/api/sync-platform")
    public ResponseEntity<Map<String, Object>> syncPlatform(@RequestParam Long platformId) {
        try {
            Platform platform = platformService.getPlatformById(platformId).orElse(null);
            if (platform == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Platform bulunamadı");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Asenkron senkronizasyon başlat
            stockSyncService.syncPlatform(platform);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Senkronizasyon başlatıldı");
            response.put("platformId", platformId);
            response.put("platformName", platform.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    // Private helper methods
    
    private Map<String, Object> getGeneralStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Toplam ürün sayısı
        long totalProducts = materialCardService.countAllMaterialCards();
        stats.put("totalProducts", totalProducts);
        
        // Toplam stok miktarı
        long totalStock = materialCardService.getTotalStock();
        stats.put("totalStock", totalStock);
        
        // Toplam stok değeri
        BigDecimal totalValue = materialCardService.getTotalValue();
        stats.put("totalValue", totalValue);
        
        // Aktif platform sayısı
        long activePlatforms = platformService.countActivePlatforms();
        stats.put("activePlatforms", activePlatforms);
        
        // Bugünkü sipariş sayısı
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        long todayOrders = orderRepository.countByPlatformAndOrderDateBetween(null, todayStart, todayEnd);
        stats.put("todayOrders", todayOrders);
        
        return stats;
    }
    
    private List<Map<String, Object>> getPlatformStatistics() {
        // Platform istatistiklerini hesapla
        return List.of(); // Implementasyon gerekli
    }
    
    private List<Map<String, Object>> getRecentSyncLogs() {
        // Son senkronizasyon loglarını getir
        return List.of(); // Implementasyon gerekli
    }
    
    private List<Map<String, Object>> getStockAlerts() {
        // Stok uyarılarını getir
        return List.of(); // Implementasyon gerekli
    }
    
    private List<Map<String, Object>> getRecentOrders() {
        // Son siparişleri getir
        return List.of(); // Implementasyon gerekli
    }
    
    private Map<String, Object> getPlatformDetailStatistics(Long platformId) {
        // Platform detay istatistiklerini hesapla
        return new HashMap<>();
    }
    
    private List<Map<String, Object>> getPlatformProducts(Long platformId) {
        // Platform'daki ürünleri getir
        return List.of();
    }
    
    private List<Map<String, Object>> getPlatformSyncLogs(Long platformId) {
        // Platform senkronizasyon loglarını getir
        return List.of();
    }
    
    private List<Map<String, Object>> getStockReport(String platformCode, String category) {
        // Stok raporu verilerini getir
        return List.of();
    }
    
    private List<String> getCategories() {
        // Kategori listesini getir
        return List.of();
    }
    
    private Map<String, Object> getSyncStatus() {
        // Senkronizasyon durumunu hesapla
        return new HashMap<>();
    }
    
    private List<Map<String, Object>> getFailedSyncs() {
        // Başarısız senkronizasyonları getir
        return List.of();
    }
    
    private List<Map<String, Object>> getPlatformSyncStatus() {
        // Platform senkronizasyon durumlarını getir
        return List.of();
    }
    
    private List<Map<String, Object>> getOrderReport(LocalDateTime start, LocalDateTime end, String platformCode) {
        // Sipariş raporu verilerini getir
        return List.of();
    }
}
