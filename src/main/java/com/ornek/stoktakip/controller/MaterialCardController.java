package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.service.MaterialCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/materials")
public class MaterialCardController {
    
    private final MaterialCardService materialCardService;
    
    @Autowired
    public MaterialCardController(MaterialCardService materialCardService) {
        this.materialCardService = materialCardService;
    }
    
    /**
     * Malzeme kartları listesi
     */
    @GetMapping
    public String materialList(Model model, 
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) String category,
                              HttpSession session) {
        
        List<MaterialCard> materials;
        
        if (search != null && !search.trim().isEmpty()) {
            materials = materialCardService.searchMaterialCards(search);
            model.addAttribute("searchTerm", search);
        } else if (type != null && !type.isEmpty()) {
            materials = materialCardService.getMaterialCardsByType(MaterialCard.MaterialType.valueOf(type));
            model.addAttribute("selectedType", type);
        } else if (category != null && !category.isEmpty()) {
            materials = materialCardService.getMaterialCardsByCategory(MaterialCard.MaterialCategory.valueOf(category));
            model.addAttribute("selectedCategory", category);
        } else {
            materials = materialCardService.getAllMaterialCards();
        }
        
        model.addAttribute("materials", materials);
        model.addAttribute("materialTypes", MaterialCard.MaterialType.values());
        model.addAttribute("materialCategories", MaterialCard.MaterialCategory.values());
        model.addAttribute("totalMaterials", materialCardService.getTotalMaterialCount());
        model.addAttribute("lowStockCount", materialCardService.getLowStockMaterialCount());
        
        return "materials/list";
    }
    
    /**
     * Yeni malzeme kartı formu
     */
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        model.addAttribute("materialCard", new MaterialCard());
        model.addAttribute("materialTypes", MaterialCard.MaterialType.values());
        model.addAttribute("materialCategories", MaterialCard.MaterialCategory.values());
        return "materials/form";
    }
    
    /**
     * Malzeme kartı düzenleme formu
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        materialCardService.getMaterialCardById(id).ifPresent(materialCard -> {
            model.addAttribute("materialCard", materialCard);
            model.addAttribute("materialTypes", MaterialCard.MaterialType.values());
            model.addAttribute("materialCategories", MaterialCard.MaterialCategory.values());
        });
        return "materials/form";
    }
    
    /**
     * Malzeme kartı kaydetme
     */
    @PostMapping("/save")
    public String saveMaterialCard(@ModelAttribute MaterialCard materialCard,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser != null) {
                materialCard.setCreatedBy(currentUser.getId());
            }
            
            materialCardService.saveMaterialCard(materialCard);
            redirectAttributes.addFlashAttribute("message", "Malzeme kartı başarıyla kaydedildi!");
            return "redirect:/materials";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/materials/new";
        }
    }
    
    /**
     * Malzeme kartı silme
     */
    @PostMapping("/delete/{id}")
    public String deleteMaterialCard(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        try {
            materialCardService.deleteMaterialCard(id);
            redirectAttributes.addFlashAttribute("message", "Malzeme kartı başarıyla silindi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/materials";
    }
    
    /**
     * Malzeme kartı detayları
     */
    @GetMapping("/view/{id}")
    public String viewMaterialCard(@PathVariable Long id, Model model, HttpSession session) {
        materialCardService.getMaterialCardById(id).ifPresent(materialCard -> {
            model.addAttribute("materialCard", materialCard);
        });
        return "materials/view";
    }
    
    /**
     * Düşük stoklu malzemeler
     */
    @GetMapping("/low-stock")
    public String lowStockMaterials(Model model, HttpSession session) {
        List<MaterialCard> lowStockMaterials = materialCardService.getLowStockMaterials();
        model.addAttribute("materials", lowStockMaterials);
        model.addAttribute("title", "Düşük Stoklu Malzemeler");
        return "materials/list";
    }
    
    /**
     * Yeniden sipariş noktasındaki malzemeler
     */
    @GetMapping("/reorder")
    public String reorderMaterials(Model model, HttpSession session) {
        List<MaterialCard> reorderMaterials = materialCardService.getMaterialsNeedingReorder();
        model.addAttribute("materials", reorderMaterials);
        model.addAttribute("title", "Yeniden Sipariş Gereken Malzemeler");
        return "materials/list";
    }
    
    /**
     * Stok güncelleme
     */
    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id,
                             @RequestParam BigDecimal quantity,
                             @RequestParam String reason,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            materialCardService.updateStock(id, quantity, reason);
            redirectAttributes.addFlashAttribute("message", "Stok başarıyla güncellendi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/materials/view/" + id;
    }
    
    /**
     * Stok düzeltme
     */
    @PostMapping("/adjust-stock/{id}")
    public String adjustStock(@PathVariable Long id,
                             @RequestParam BigDecimal newQuantity,
                             @RequestParam String reason,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        try {
            materialCardService.adjustStock(id, newQuantity, reason);
            redirectAttributes.addFlashAttribute("message", "Stok başarıyla düzeltildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/materials/view/" + id;
    }
    
    /**
     * Malzeme kartı arama (AJAX)
     */
    @GetMapping("/search")
    @ResponseBody
    public List<MaterialCard> searchMaterials(@RequestParam String query) {
        return materialCardService.searchMaterialCards(query);
    }
    
    /**
     * Malzeme kartı detayları (AJAX)
     */
    @GetMapping("/details/{id}")
    @ResponseBody
    public MaterialCard getMaterialDetails(@PathVariable Long id) {
        return materialCardService.getMaterialCardById(id).orElse(null);
    }
    
    /**
     * Stok değeri raporu
     */
    @GetMapping("/stock-value")
    public String stockValueReport(Model model, HttpSession session) {
        BigDecimal totalValue = materialCardService.calculateTotalStockValue();
        BigDecimal rawMaterialValue = materialCardService.calculateStockValueByType(MaterialCard.MaterialType.RAW_MATERIAL);
        BigDecimal finishedGoodsValue = materialCardService.calculateStockValueByType(MaterialCard.MaterialType.FINISHED_GOOD);
        
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("rawMaterialValue", rawMaterialValue);
        model.addAttribute("finishedGoodsValue", finishedGoodsValue);
        
        return "materials/stock-value-report";
    }
    
    /**
     * Malzeme kartı istatistikleri
     */
    @GetMapping("/statistics")
    public String materialStatistics(Model model, HttpSession session) {
        model.addAttribute("totalMaterials", materialCardService.getTotalMaterialCount());
        model.addAttribute("activeMaterials", materialCardService.getActiveMaterialCount());
        model.addAttribute("lowStockMaterials", materialCardService.getLowStockMaterialCount());
        model.addAttribute("totalValue", materialCardService.calculateTotalStockValue());
        
        return "materials/statistics";
    }
}
