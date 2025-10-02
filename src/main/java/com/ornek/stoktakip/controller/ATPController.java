package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.service.ATPService;
import com.ornek.stoktakip.service.ATPService.*;
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
@RequestMapping("/atp")
public class ATPController {
    
    private final ATPService atpService;
    
    @Autowired
    public ATPController(ATPService atpService) {
        this.atpService = atpService;
    }
    
    /**
     * ATP ana sayfası
     */
    @GetMapping
    public String atpHome(Model model, HttpSession session) {
        return "atp/home";
    }
    
    /**
     * Malzeme ATP hesaplama
     */
    @GetMapping("/calculate/{materialId}")
    public String calculateATP(@PathVariable Long materialId, Model model, HttpSession session) {
        try {
            ATPResult atpResult = atpService.calculateATP(materialId);
            if (atpResult == null) {
                model.addAttribute("error", "Malzeme bulunamadı!");
                return "atp/calculate";
            }
            
            model.addAttribute("atpResult", atpResult);
            model.addAttribute("constraints", atpResult.getConstraints());
            model.addAttribute("hasConstraints", atpResult.hasConstraints());
            
            return "atp/calculate";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/calculate";
        }
    }
    
    /**
     * ATP hesaplama (AJAX)
     */
    @PostMapping("/calculate-ajax")
    @ResponseBody
    public ATPResult calculateATPAjax(@RequestParam Long materialId) {
        try {
            return atpService.calculateATP(materialId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * ATP raporu
     */
    @GetMapping("/report/{materialId}")
    public String generateATPReport(@PathVariable Long materialId, Model model, HttpSession session) {
        try {
            ATPReport report = atpService.generateATPReport(materialId);
            if (report == null) {
                model.addAttribute("error", "Rapor oluşturulamadı!");
                return "atp/report";
            }
            
            model.addAttribute("report", report);
            model.addAttribute("atpResult", report.getAtpResult());
            model.addAttribute("constraints", report.getConstraints());
            model.addAttribute("platformStocks", report.getPlatformStocks());
            
            return "atp/report";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/report";
        }
    }
    
    /**
     * Stok kısıtlama analizi
     */
    @GetMapping("/constraints/{materialId}")
    public String analyzeStockConstraints(@PathVariable Long materialId, Model model, HttpSession session) {
        try {
            StockConstraintAnalysis analysis = atpService.analyzeStockConstraints(materialId);
            if (analysis == null) {
                model.addAttribute("error", "Analiz yapılamadı!");
                return "atp/constraints";
            }
            
            model.addAttribute("analysis", analysis);
            model.addAttribute("primaryConstraints", analysis.getPrimaryConstraints());
            model.addAttribute("secondaryConstraints", analysis.getSecondaryConstraints());
            
            return "atp/constraints";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/constraints";
        }
    }
    
    /**
     * Stok kısıtlama önerileri
     */
    @GetMapping("/recommendations/{materialId}")
    public String getStockConstraintRecommendations(@PathVariable Long materialId, Model model, HttpSession session) {
        try {
            List<StockConstraintRecommendation> recommendations = atpService.getStockConstraintRecommendations(materialId);
            
            model.addAttribute("materialId", materialId);
            model.addAttribute("recommendations", recommendations);
            
            return "atp/recommendations";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/recommendations";
        }
    }
    
    /**
     * Platform stok senkronizasyonu
     */
    @PostMapping("/sync-platform/{platformId}")
    public String syncPlatformStocks(@PathVariable Long platformId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            PlatformStockSyncResult result = atpService.syncPlatformStocks(platformId);
            if (result == null) {
                redirectAttributes.addFlashAttribute("error", "Platform bulunamadı!");
                return "redirect:/platforms";
            }
            
            redirectAttributes.addFlashAttribute("message", 
                "Platform stokları senkronize edildi. Başarılı: " + result.getSyncedProducts() + 
                ", Hatalı: " + result.getFailedProducts());
            
            return "redirect:/platforms";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/platforms";
        }
    }
    
    /**
     * Tüm platform stoklarını senkronize et
     */
    @PostMapping("/sync-all-platforms")
    public String syncAllPlatformStocks(RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            boolean success = atpService.updateAllPlatformStocks();
            
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Tüm platform stokları başarıyla senkronize edildi!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bazı platform stokları senkronize edilemedi!");
            }
            
            return "redirect:/platforms";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/platforms";
        }
    }
    
    /**
     * Malzeme stoklarını tüm platformlarda güncelle
     */
    @PostMapping("/update-material-stocks/{materialId}")
    public String updateMaterialStockOnAllPlatforms(@PathVariable Long materialId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            boolean success = atpService.updateMaterialStockOnAllPlatforms(materialId);
            
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Malzeme stokları tüm platformlarda güncellendi!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bazı platformlarda güncelleme başarısız!");
            }
            
            return "redirect:/materials/view/" + materialId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/materials/view/" + materialId;
        }
    }
    
    /**
     * ATP hesaplama geçmişi
     */
    @GetMapping("/history/{materialId}")
    public String getATPCalculationHistory(@PathVariable Long materialId, Model model, HttpSession session) {
        try {
            List<ATPCalculationHistory> history = atpService.getATPCalculationHistory(materialId);
            
            model.addAttribute("materialId", materialId);
            model.addAttribute("history", history);
            
            return "atp/history";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/history";
        }
    }
    
    /**
     * ATP karşılaştırma
     */
    @GetMapping("/compare")
    public String compareATP(@RequestParam List<Long> materialIds, Model model, HttpSession session) {
        try {
            List<ATPResult> atpResults = new ArrayList<>();
            
            for (Long materialId : materialIds) {
                ATPResult result = atpService.calculateATP(materialId);
                if (result != null) {
                    atpResults.add(result);
                }
            }
            
            model.addAttribute("atpResults", atpResults);
            model.addAttribute("materialIds", materialIds);
            
            return "atp/compare";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hata: " + e.getMessage());
            return "atp/compare";
        }
    }
    
    /**
     * ATP istatistikleri
     */
    @GetMapping("/statistics")
    public String atpStatistics(Model model, HttpSession session) {
        // İstatistikler burada hesaplanacak
        model.addAttribute("totalMaterials", 0);
        model.addAttribute("materialsWithConstraints", 0);
        model.addAttribute("averageATP", BigDecimal.ZERO);
        model.addAttribute("totalPlatforms", 0);
        
        return "atp/statistics";
    }
    
    /**
     * ATP hesaplama (AJAX) - Toplu
     */
    @PostMapping("/calculate-bulk-ajax")
    @ResponseBody
    public List<ATPResult> calculateATPBulkAjax(@RequestParam List<Long> materialIds) {
        try {
            List<ATPResult> results = new ArrayList<>();
            
            for (Long materialId : materialIds) {
                ATPResult result = atpService.calculateATP(materialId);
                if (result != null) {
                    results.add(result);
                }
            }
            
            return results;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Platform stok durumu (AJAX)
     */
    @GetMapping("/platform-stock-status/{platformId}")
    @ResponseBody
    public PlatformStockSyncResult getPlatformStockStatus(@PathVariable Long platformId) {
        try {
            return atpService.syncPlatformStocks(platformId);
        } catch (Exception e) {
            return null;
        }
    }
}
