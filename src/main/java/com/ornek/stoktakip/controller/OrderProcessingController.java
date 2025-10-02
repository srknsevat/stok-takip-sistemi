package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Order;
import com.ornek.stoktakip.service.OrderProcessingService;
import com.ornek.stoktakip.service.BomExplosionService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order-processing")
public class OrderProcessingController {
    
    private final OrderProcessingService orderProcessingService;
    
    @Autowired
    public OrderProcessingController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }
    
    /**
     * Sipariş işleme ana sayfası
     */
    @GetMapping
    public String orderProcessingHome(Model model, HttpSession session) {
        return "order-processing/home";
    }
    
    /**
     * Siparişi işle ve stoktan düş
     */
    @PostMapping("/process/{orderId}")
    public String processOrder(@PathVariable Long orderId,
                              @RequestParam(required = false) Boolean useBOM,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            OrderProcessingService.OrderProcessingResult result;
            
            if (useBOM != null && useBOM) {
                result = orderProcessingService.processOrderWithBOM(order);
            } else {
                result = orderProcessingService.processOrder(order);
            }
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("message", "Sipariş başarıyla işlendi: " + result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", "Sipariş işleme hatası: " + result.getMessage());
            }
            
            return "redirect:/orders";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/orders";
        }
    }
    
    /**
     * Sipariş stok kontrolü
     */
    @GetMapping("/check-stock/{orderId}")
    public String checkOrderStock(@PathVariable Long orderId, Model model, HttpSession session) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            OrderProcessingService.OrderStockCheckResult checkResult = orderProcessingService.checkOrderStock(order);
            
            model.addAttribute("order", order);
            model.addAttribute("checkResult", checkResult);
            model.addAttribute("shortages", checkResult.getShortages());
            
            return "order-processing/stock-check";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "order-processing/stock-check";
        }
    }
    
    /**
     * Sipariş maliyet hesaplama
     */
    @GetMapping("/calculate-cost/{orderId}")
    public String calculateOrderCost(@PathVariable Long orderId, Model model, HttpSession session) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            OrderProcessingService.OrderCostResult costResult = orderProcessingService.calculateOrderCost(order);
            
            model.addAttribute("order", order);
            model.addAttribute("costResult", costResult);
            
            return "order-processing/cost-calculation";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "order-processing/cost-calculation";
        }
    }
    
    /**
     * Sipariş üretim planı
     */
    @GetMapping("/production-plan/{orderId}")
    public String createOrderProductionPlan(@PathVariable Long orderId, Model model, HttpSession session) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            OrderProcessingService.OrderProductionPlan productionPlan = orderProcessingService.createOrderProductionPlan(order);
            
            model.addAttribute("order", order);
            model.addAttribute("productionPlan", productionPlan);
            model.addAttribute("mrpItems", productionPlan.getMrpItems());
            
            return "order-processing/production-plan";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "order-processing/production-plan";
        }
    }
    
    /**
     * Sipariş işleme raporu
     */
    @GetMapping("/report/{orderId}")
    public String generateOrderProcessingReport(@PathVariable Long orderId, Model model, HttpSession session) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            OrderProcessingService.OrderProcessingReport report = orderProcessingService.generateOrderProcessingReport(order);
            
            model.addAttribute("order", order);
            model.addAttribute("report", report);
            
            return "order-processing/report";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "order-processing/report";
        }
    }
    
    /**
     * BOM patlatma (AJAX)
     */
    @PostMapping("/explode-bom")
    @ResponseBody
    public Map<String, BomExplosionResult> explodeBOM(@RequestParam Long materialId,
                                                     @RequestParam BigDecimal quantity) {
        // Bu kısım BomExplosionService'den gelecek
        return Map.of();
    }
    
    /**
     * Stok kontrolü (AJAX)
     */
    @PostMapping("/check-stock-ajax")
    @ResponseBody
    public OrderProcessingService.OrderStockCheckResult checkStockAjax(@RequestParam Long orderId) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            return orderProcessingService.checkOrderStock(order);
            
        } catch (Exception e) {
            OrderProcessingService.OrderStockCheckResult errorResult = new OrderProcessingService.OrderStockCheckResult();
            errorResult.setStockAvailable(false);
            errorResult.setMessage("Hata: " + e.getMessage());
            return errorResult;
        }
    }
    
    /**
     * Maliyet hesaplama (AJAX)
     */
    @PostMapping("/calculate-cost-ajax")
    @ResponseBody
    public OrderProcessingService.OrderCostResult calculateCostAjax(@RequestParam Long orderId) {
        try {
            // Siparişi bul (bu kısım OrderService'den gelecek)
            Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
            
            return orderProcessingService.calculateOrderCost(order);
            
        } catch (Exception e) {
            OrderProcessingService.OrderCostResult errorResult = new OrderProcessingService.OrderCostResult();
            errorResult.setTotalCost(BigDecimal.ZERO);
            return errorResult;
        }
    }
    
    /**
     * Sipariş işleme istatistikleri
     */
    @GetMapping("/statistics")
    public String orderProcessingStatistics(Model model, HttpSession session) {
        // İstatistikler burada hesaplanacak
        model.addAttribute("totalOrders", 0);
        model.addAttribute("processedOrders", 0);
        model.addAttribute("pendingOrders", 0);
        model.addAttribute("totalValue", BigDecimal.ZERO);
        
        return "order-processing/statistics";
    }
    
    /**
     * Toplu sipariş işleme
     */
    @PostMapping("/bulk-process")
    public String bulkProcessOrders(@RequestParam List<Long> orderIds,
                                   @RequestParam(required = false) Boolean useBOM,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        try {
            int successCount = 0;
            int errorCount = 0;
            
            for (Long orderId : orderIds) {
                try {
                    // Siparişi bul (bu kısım OrderService'den gelecek)
                    Order order = new Order(); // Placeholder - gerçek implementasyon gerekli
                    
                    OrderProcessingService.OrderProcessingResult result;
                    
                    if (useBOM != null && useBOM) {
                        result = orderProcessingService.processOrderWithBOM(order);
                    } else {
                        result = orderProcessingService.processOrder(order);
                    }
                    
                    if (result.isSuccess()) {
                        successCount++;
                    } else {
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    errorCount++;
                }
            }
            
            redirectAttributes.addFlashAttribute("message", 
                "Toplu işlem tamamlandı. Başarılı: " + successCount + ", Hatalı: " + errorCount);
            
            return "redirect:/orders";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Toplu işlem hatası: " + e.getMessage());
            return "redirect:/orders";
        }
    }
}
