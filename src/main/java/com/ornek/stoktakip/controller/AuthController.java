package com.ornek.stoktakip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                       @RequestParam(required = false) String logout,
                       @RequestParam(required = false) String expired,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Geçersiz kullanıcı adı veya şifre");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Başarıyla çıkış yaptınız");
        }
        
        if (expired != null) {
            model.addAttribute("message", "Oturumunuz süresi doldu. Lütfen tekrar giriş yapın");
        }
        
        return "auth/login";
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/auth/login?logout=true";
    }
}