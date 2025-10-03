package com.ornek.stoktakip.controller;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.entity.Role;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    
    private final UserService userService;
    
    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Kullanıcı listesi
     */
    @GetMapping
    public String userList(Model model, 
                          @RequestParam(required = false) String search,
                          HttpSession session) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("users", userService.searchUsers(search));
            model.addAttribute("searchTerm", search);
        } else {
            model.addAttribute("users", userService.getAllUsers());
        }
        
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("activeUsers", userService.getActiveUserCount());
        
        return "admin/users/list";
    }
    
    /**
     * Yeni kullanıcı formu
     */
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        return "admin/users/form";
    }
    
    /**
     * Kullanıcı düzenleme formu
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        userService.getUserById(id).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", User.Role.values());
        });
        return "admin/users/form";
    }
    
    /**
     * Kullanıcı kaydetme
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                          @RequestParam(required = false) String newPassword,
                          @RequestParam(required = false) String confirmPassword,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            User currentUser = (User) session.getAttribute("user");
            
            if (user.getId() == null) {
                // Yeni kullanıcı
                if (userService.isUserExists(user.getUsername(), user.getEmail())) {
                    redirectAttributes.addFlashAttribute("error", "Kullanıcı adı veya e-posta zaten kullanımda!");
                    return "redirect:/admin/users/new";
                }
                
                userService.createUserByAdmin(user.getUsername(), user.getEmail(), 
                                            newPassword, user.getFirstName(), 
                                            user.getLastName(), user.getRoles(), 
                                            currentUser.getId());
            } else {
                // Mevcut kullanıcı güncelleme
                if (newPassword != null && !newPassword.isEmpty()) {
                    if (!newPassword.equals(confirmPassword)) {
                        redirectAttributes.addFlashAttribute("error", "Şifreler eşleşmiyor!");
                        return "redirect:/admin/users/edit/" + user.getId();
                    }
                    user.setPassword(newPassword);
                }
                userService.updateUser(user);
            }
            
            redirectAttributes.addFlashAttribute("message", "Kullanıcı başarıyla kaydedildi!");
            return "redirect:/admin/users";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
            return user.getId() == null ? "redirect:/admin/users/new" : "redirect:/admin/users/edit/" + user.getId();
        }
    }
    
    /**
     * Kullanıcı silme
     */
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Kendi hesabınızı silemezsiniz!");
                return "redirect:/admin/users";
            }
            
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Kullanıcı başarıyla silindi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    /**
     * Kullanıcı kilitleme/açma
     */
    @PostMapping("/toggle-lock/{id}")
    public String toggleUserLock(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            User user = userService.getUserById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı bulunamadı!");
                return "redirect:/admin/users";
            }
            
            if (user.isAccountNonLocked()) {
                userService.lockUser(id);
                redirectAttributes.addFlashAttribute("message", "Kullanıcı kilitlendi!");
            } else {
                userService.unlockUser(id);
                redirectAttributes.addFlashAttribute("message", "Kullanıcı kilidi açıldı!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    /**
     * Kullanıcı aktif/pasif durumu değiştirme
     */
    @PostMapping("/toggle-active/{id}")
    public String toggleUserActive(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            User user = userService.getUserById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı bulunamadı!");
                return "redirect:/admin/users";
            }
            
            user.setIsActive(!user.getIsActive());
            userService.saveUser(user);
            
            String status = user.getIsActive() ? "aktif" : "pasif";
            redirectAttributes.addFlashAttribute("message", "Kullanıcı " + status + " yapıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    /**
     * Kullanıcı rol atama
     */
    @PostMapping("/assign-role/{id}")
    public String assignRole(@PathVariable Long id,
                            @RequestParam Set<User.Role> roles,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        try {
            userService.assignRoles(id, roles);
            redirectAttributes.addFlashAttribute("message", "Kullanıcı rolleri güncellendi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Hata: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    /**
     * Kullanıcı detayları
     */
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model, HttpSession session) {
        // Yetki kontrolü
        if (!hasAdminRole(session)) {
            return "redirect:/auth/login";
        }
        
        userService.getUserById(id).ifPresent(user -> {
            model.addAttribute("user", user);
        });
        return "admin/users/view";
    }
    
    /**
     * Admin yetkisi kontrolü
     */
    private boolean hasAdminRole(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.hasAnyRole(User.Role.SUPER_ADMIN, User.Role.ADMIN);
    }
}
