package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/platforms")
public class PlatformController {
    
    private final PlatformService platformService;
    
    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }
    
    @GetMapping
    public String platformList(Model model) {
        List<Platform> platforms = platformService.getActivePlatforms();
        model.addAttribute("platforms", platforms);
        return "platforms/list";
    }
    
    @GetMapping("/create")
    public String createPlatformForm(Model model) {
        model.addAttribute("platform", new Platform());
        return "platforms/create";
    }
    
    @PostMapping("/create")
    public String createPlatform(@ModelAttribute Platform platform) {
        try {
            platformService.createPlatform(platform.getName(), platform.getCode(), platform.getDescription());
            return "redirect:/platforms?success=created";
        } catch (Exception e) {
            return "redirect:/platforms/create?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editPlatformForm(@PathVariable Long id, Model model) {
        Platform platform = platformService.getPlatformById(id).orElse(null);
        if (platform != null) {
            model.addAttribute("platform", platform);
            return "platforms/edit";
        }
        return "redirect:/platforms?error=platform_not_found";
    }
    
    @PostMapping("/edit/{id}")
    public String updatePlatform(@PathVariable Long id, @ModelAttribute Platform platform) {
        try {
            platform.setId(id);
            platformService.updatePlatform(platform);
            return "redirect:/platforms?success=updated";
        } catch (Exception e) {
            return "redirect:/platforms/edit/" + id + "?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deletePlatform(@PathVariable Long id) {
        try {
            platformService.deletePlatform(id);
            return "redirect:/platforms?success=deleted";
        } catch (Exception e) {
            return "redirect:/platforms?error=" + e.getMessage();
        }
    }
    
    // API Endpoints
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<Platform>> getPlatformsApi() {
        List<Platform> platforms = platformService.getActivePlatforms();
        return ResponseEntity.ok(platforms);
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Platform> getPlatformApi(@PathVariable Long id) {
        Platform platform = platformService.getPlatformById(id).orElse(null);
        if (platform != null) {
            return ResponseEntity.ok(platform);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPlatformApi(@RequestBody Platform platform) {
        Map<String, Object> response = new HashMap<>();
        try {
            Platform createdPlatform = platformService.createPlatform(platform.getName(), platform.getCode(), platform.getDescription());
            response.put("success", true);
            response.put("platform", createdPlatform);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
