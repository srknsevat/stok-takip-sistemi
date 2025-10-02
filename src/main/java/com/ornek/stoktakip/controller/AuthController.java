package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Giriş sayfası
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Kullanıcı adı veya şifre hatalı!");
        }
        return "auth/login";
    }
    
    /**
     * Giriş işlemi
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            if (userService.authenticateUser(username, password)) {
                User user = userService.getUserByUsername(username).orElse(null);
                if (user != null && user.isEnabled()) {
                    session.setAttribute("user", user);
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("userRoles", user.getRoles());
                    return "redirect:/";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Hesabınız aktif değil veya e-posta doğrulanmamış!");
                    return "redirect:/auth/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı adı veya şifre hatalı!");
                return "redirect:/auth/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Giriş sırasında hata oluştu: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }
    
    /**
     * Çıkış işlemi
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
    
    /**
     * Kayıt sayfası
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    /**
     * Kayıt işlemi
     */
    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            // Şifre kontrolü
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Şifreler eşleşmiyor!");
                return "redirect:/auth/register";
            }
            
            // Kullanıcı adı ve e-posta kontrolü
            if (userService.isUserExists(user.getUsername(), user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı adı veya e-posta zaten kullanımda!");
                return "redirect:/auth/register";
            }
            
            // Varsayılan rol: VIEWER
            user.setRoles(Set.of(User.Role.VIEWER));
            
            userService.createUser(user.getUsername(), user.getEmail(), user.getPassword(), 
                                 user.getFirstName(), user.getLastName(), user.getRoles());
            
            redirectAttributes.addFlashAttribute("message", "Kayıt başarılı! E-posta doğrulama linkini kontrol edin.");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Kayıt sırasında hata oluştu: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
    
    /**
     * Şifre sıfırlama sayfası
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "auth/forgot-password";
    }
    
    /**
     * Şifre sıfırlama işlemi
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(email);
            redirectAttributes.addFlashAttribute("message", "Şifre sıfırlama linki e-posta adresinize gönderildi!");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }
    
    /**
     * Şifre sıfırlama formu
     */
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        if (userService.validatePasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "auth/reset-password";
        } else {
            model.addAttribute("error", "Geçersiz veya süresi dolmuş token!");
            return "auth/reset-password";
        }
    }
    
    /**
     * Şifre sıfırlama işlemi
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Şifreler eşleşmiyor!");
                return "redirect:/auth/reset-password?token=" + token;
            }
            
            userService.updatePasswordWithToken(token, newPassword);
            redirectAttributes.addFlashAttribute("message", "Şifre başarıyla güncellendi!");
            return "redirect:/auth/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }
    
    /**
     * E-posta doğrulama
     */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token,
                             RedirectAttributes redirectAttributes) {
        try {
            if (userService.verifyEmail(token)) {
                redirectAttributes.addFlashAttribute("message", "E-posta başarıyla doğrulandı!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Geçersiz veya süresi dolmuş token!");
            }
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return "redirect:/auth/login";
        }
    }
}
