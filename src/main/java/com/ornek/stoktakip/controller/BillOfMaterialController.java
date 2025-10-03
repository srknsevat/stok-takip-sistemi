package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.BillOfMaterial;
import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.service.BillOfMaterialService;
import com.ornek.stoktakip.service.MaterialCardService;
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
@RequestMapping("/bom")
public class BillOfMaterialController {
    
    private final BillOfMaterialService bomService;
    private final MaterialCardService materialCardService;
    
    @Autowired
    public BillOfMaterialController(BillOfMaterialService bomService, MaterialCardService materialCardService) {
        this.bomService = bomService;
        this.materialCardService = materialCardService;
    }
    
    /**
     * BOM listesi
     */
    @GetMapping
    public String bomList(Model model, 
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) String type,
                         HttpSession session) {
        
        List<BillOfMaterial> boms;
        
        if (search != null && !search.trim().isEmpty()) {
            boms = bomService.searchBOMs(search);
            model.addAttribute("searchTerm", search);
        } else if (type != null && !type.isEmpty()) {
            boms = bomService.getBOMsByType(BillOfMaterial.BomType.valueOf(type));
            model.addAttribute("selectedType", type);
        } else {
            boms = bomService.getAllBOMs();
        }
        
        model.addAttribute("boms", boms);
        model.addAttribute("bomTypes", BillOfMaterial.BomType.values());
        model.addAttribute("totalBOMs", bomService.getBOMCount());
        
        return "bom/list";
    }
    
    /**
     * Yeni BOM formu
     */
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        model.addAttribute("bom", new BillOfMaterial());
        model.addAttribute("materials", materialCardService.getAllMaterialCards());
        model.addAttribute("bomTypes", BillOfMaterial.BomType.values());
        return "bom/form";
    }
    
    /**
     * BOM düzenleme formu
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        BillOfMaterial bom = bomService.getBOMById(id);
        model.addAttribute("bom", bom);
        model.addAttribute("materials", materialCardService.getAllMaterialCards());
        model.addAttribute("bomTypes", BillOfMaterial.BomType.values());
        return "bom/form";
    }
    
    /**
     * BOM kaydetme
     */
    @PostMapping("/save")
    public String saveBOM(@ModelAttribute BillOfMaterial bom,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser != null) {
                bom.setCreatedBy(currentUser.getId());
            }
            
            bomService.saveBOM(bom);
            redirectAttributes.addFlashAttribute("message", "BOM başarıyla kaydedildi!");
            return "redirect:/bom";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/bom/new";
        }
    }
    
    /**
     * BOM silme
     */
    @PostMapping("/delete/{id}")
    public String deleteBOM(@PathVariable Long id,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {
        try {
            bomService.deleteBOM(id);
            redirectAttributes.addFlashAttribute("message", "BOM başarıyla silindi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/bom";
    }
    
    /**
     * BOM detayları
     */
    @GetMapping("/view/{id}")
    public String viewBOM(@PathVariable Long id, Model model, HttpSession session) {
        BillOfMaterial bom = bomService.getBOMById(id);
        model.addAttribute("bom", bom);
        return "bom/view";
    }
    
    /**
     * Malzeme BOM yapısı
     */
    @GetMapping("/structure/{materialId}")
    public String bomStructure(@PathVariable Long materialId, Model model, HttpSession session) {
        List<BillOfMaterial> structure = bomService.getBOMStructure(materialId);
        MaterialCard material = materialCardService.getMaterialCardById(materialId).orElse(null);
        
        model.addAttribute("structure", structure);
        model.addAttribute("material", material);
        model.addAttribute("bomTree", bomService.getBOMTree(materialId));
        
        return "bom/structure";
    }
    
    /**
     * BOM ağacı görünümü
     */
    @GetMapping("/tree/{materialId}")
    public String bomTree(@PathVariable Long materialId, Model model, HttpSession session) {
        Map<String, Object> bomTree = bomService.getBOMTree(materialId);
        MaterialCard material = materialCardService.getMaterialCardById(materialId).orElse(null);
        
        model.addAttribute("bomTree", bomTree);
        model.addAttribute("material", material);
        
        return "bom/tree";
    }
    
    /**
     * BOM maliyet hesaplama
     */
    @GetMapping("/cost/{materialId}")
    public String bomCost(@PathVariable Long materialId, 
                         @RequestParam(defaultValue = "1") BigDecimal quantity,
                         Model model, HttpSession session) {
        
        MaterialCard material = materialCardService.getMaterialCardById(materialId).orElse(null);
        Map<String, BigDecimal> costedBOM = bomService.calculateCostedBOM(materialId, quantity);
        BigDecimal totalCost = bomService.calculateTotalCost(materialId, quantity);
        
        model.addAttribute("material", material);
        model.addAttribute("quantity", quantity);
        model.addAttribute("costedBOM", costedBOM);
        model.addAttribute("totalCost", totalCost);
        
        return "bom/cost";
    }
    
    /**
     * BOM maliyet hesaplama (AJAX)
     */
    @PostMapping("/calculate-cost")
    @ResponseBody
    public Map<String, Object> calculateCost(@RequestParam Long materialId,
                                           @RequestParam BigDecimal quantity) {
        Map<String, BigDecimal> costedBOM = bomService.calculateCostedBOM(materialId, quantity);
        BigDecimal totalCost = bomService.calculateTotalCost(materialId, quantity);
        
        return Map.of(
            "costedBOM", costedBOM,
            "totalCost", totalCost
        );
    }
    
    /**
     * BOM doğrulama
     */
    @GetMapping("/validate/{materialId}")
    public String validateBOM(@PathVariable Long materialId, Model model, HttpSession session) {
        boolean isValid = bomService.validateBOMStructure(materialId);
        boolean hasCircularRef = bomService.hasCircularReference(materialId);
        List<String> circularRefs = bomService.getCircularReferences(materialId);
        List<String> validationErrors = bomService.getBOMValidationErrors(materialId);
        
        MaterialCard material = materialCardService.getMaterialCardById(materialId).orElse(null);
        
        model.addAttribute("material", material);
        model.addAttribute("isValid", isValid);
        model.addAttribute("hasCircularRef", hasCircularRef);
        model.addAttribute("circularRefs", circularRefs);
        model.addAttribute("validationErrors", validationErrors);
        
        return "bom/validation";
    }
    
    /**
     * BOM karşılaştırma
     */
    @GetMapping("/compare")
    public String compareBOMs(@RequestParam Long bom1Id,
                             @RequestParam Long bom2Id,
                             Model model, HttpSession session) {
        
        List<BillOfMaterial> comparison = bomService.compareBOMs(bom1Id, bom2Id);
        Map<String, Object> differences = bomService.getBOMDifferences(bom1Id, bom2Id);
        
        model.addAttribute("comparison", comparison);
        model.addAttribute("differences", differences);
        
        return "bom/compare";
    }
    
    /**
     * BOM raporu
     */
    @GetMapping("/report/{materialId}")
    public String bomReport(@PathVariable Long materialId, Model model, HttpSession session) {
        List<Map<String, Object>> report = bomService.getBOMReport(materialId);
        MaterialCard material = materialCardService.getMaterialCardById(materialId).orElse(null);
        
        model.addAttribute("report", report);
        model.addAttribute("material", material);
        
        return "bom/report";
    }
    
    /**
     * BOM kopyalama
     */
    @PostMapping("/copy")
    public String copyBOM(@RequestParam Long sourceBOMId,
                         @RequestParam Long newParentMaterialId,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {
        try {
            MaterialCard newParent = materialCardService.getMaterialCardById(newParentMaterialId).orElse(null);
            if (newParent != null) {
                bomService.copyBOM(sourceBOMId, newParent);
                redirectAttributes.addFlashAttribute("message", "BOM başarıyla kopyalandı!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Hedef malzeme bulunamadı!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        return "redirect:/bom";
    }
    
    /**
     * BOM arama (AJAX)
     */
    @GetMapping("/search")
    @ResponseBody
    public List<BillOfMaterial> searchBOMs(@RequestParam String query) {
        return bomService.searchBOMs(query);
    }
    
    /**
     * BOM istatistikleri
     */
    @GetMapping("/statistics")
    public String bomStatistics(Model model, HttpSession session) {
        model.addAttribute("totalBOMs", bomService.getBOMCount());
        model.addAttribute("productionBOMs", bomService.getBOMCountByType(BillOfMaterial.BomType.PRODUCTION));
        model.addAttribute("engineeringBOMs", bomService.getBOMCountByType(BillOfMaterial.BomType.ENGINEERING));
        
        return "bom/statistics";
    }
}
