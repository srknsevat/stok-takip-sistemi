package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.PlatformTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/platform-config")
public class PlatformConfigController {
    
    private final PlatformService platformService;
    private final PlatformTestService platformTestService;
    
    @Autowired
    public PlatformConfigController(PlatformService platformService, 
                                  PlatformTestService platformTestService) {
        this.platformService = platformService;
        this.platformTestService = platformTestService;
    }
    
    /**
     * Platform konfigürasyon listesi
     */
    @GetMapping
    public String platformList(Model model) {
        model.addAttribute("platforms", platformService.getAllPlatforms());
        return "platform-config/list";
    }
    
    /**
     * Yeni platform ekleme formu
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("platform", new Platform());
        model.addAttribute("platformTypes", getPlatformTypes());
        return "platform-config/form";
    }
    
    /**
     * Platform düzenleme formu
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        platformService.getPlatformById(id).ifPresent(platform -> {
            model.addAttribute("platform", platform);
            model.addAttribute("platformTypes", getPlatformTypes());
        });
        return "platform-config/form";
    }
    
    /**
     * Platform kaydetme
     */
    @PostMapping("/save")
    public String savePlatform(@ModelAttribute Platform platform, 
                              RedirectAttributes redirectAttributes,
                              Locale locale) {
        try {
            platformService.savePlatform(platform);
            redirectAttributes.addFlashAttribute("message", "Platform başarıyla kaydedildi");
            return "redirect:/platform-config";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/platform-config/new";
        }
    }
    
    /**
     * Platform silme
     */
    @PostMapping("/delete/{id}")
    public String deletePlatform(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            platformService.deletePlatform(id);
            redirectAttributes.addFlashAttribute("message", "Platform başarıyla silindi");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/platform-config";
    }
    
    /**
     * Platform bağlantı testi
     */
    @PostMapping("/test-connection/{id}")
    public String testConnection(@PathVariable Long id, 
                                RedirectAttributes redirectAttributes) {
        try {
            Platform platform = platformService.getPlatformById(id).orElse(null);
            if (platform == null) {
                redirectAttributes.addFlashAttribute("error", "Platform bulunamadı");
                return "redirect:/platform-config";
            }
            
            // Kimlik bilgilerini çöz
            Platform decryptedPlatform = ((com.ornek.stoktakip.service.impl.PlatformServiceImpl) platformService)
                    .getPlatformWithDecryptedCredentials(id);
            
            // Bağlantıyı test et
            boolean isConnected = platformTestService.testPlatformConnection(decryptedPlatform);
            
            if (isConnected) {
                redirectAttributes.addFlashAttribute("message", "Platform bağlantısı başarılı! ✅");
            } else {
                redirectAttributes.addFlashAttribute("error", "Platform bağlantısı başarısız! ❌");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Bağlantı testi hatası: " + e.getMessage());
        }
        return "redirect:/platform-config";
    }
    
    /**
     * Platform API kimlik bilgileri güncelleme
     */
    @GetMapping("/credentials/{id}")
    public String showCredentialsForm(@PathVariable Long id, Model model) {
        platformService.getPlatformById(id).ifPresent(platform -> {
            model.addAttribute("platform", platform);
        });
        return "platform-config/credentials";
    }
    
    /**
     * API kimlik bilgilerini kaydetme
     */
    @PostMapping("/save-credentials/{id}")
    public String saveCredentials(@PathVariable Long id,
                                 @RequestParam String apiKey,
                                 @RequestParam String apiSecret,
                                 @RequestParam(required = false) String accessToken,
                                 @RequestParam(required = false) String refreshToken,
                                 @RequestParam(required = false) String webhookUrl,
                                 RedirectAttributes redirectAttributes) {
        try {
            platformService.updatePlatformCredentials(id, apiKey, apiSecret, accessToken);
            redirectAttributes.addFlashAttribute("message", "API kimlik bilgileri başarıyla güncellendi");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/platform-config/credentials/" + id;
    }
    
    /**
     * Platform senkronizasyonu başlatma
     */
    @PostMapping("/sync/{id}")
    public String syncPlatform(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes) {
        try {
            // Senkronizasyon işlemini başlat
            redirectAttributes.addFlashAttribute("message", "Platform senkronizasyonu başlatıldı");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Senkronizasyon hatası: " + e.getMessage());
        }
        return "redirect:/platform-config";
    }
    
    private String[] getPlatformTypes() {
        return new String[]{"eBay", "Shopify", "Amazon", "Trendyol", "Hepsiburada", "N11", "GittiGidiyor"};
    }
}
