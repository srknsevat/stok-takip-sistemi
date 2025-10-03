package com.ornek.stoktakip.config;

import com.ornek.stoktakip.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Giriş sayfaları ve statik kaynaklar için kontrol yapma
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/") || 
            requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") ||
            requestURI.startsWith("/h2-console/") ||
            requestURI.startsWith("/webhooks/")) {
            return true;
        }
        
        // Session kontrolü
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/auth/login");
            return false;
        }
        
        // Kullanıcı durumu kontrolü
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isEnabled()) {
            session.invalidate();
            response.sendRedirect("/auth/login?disabled=true");
            return false;
        }
        
        // Hesap kilit kontrolü
        if (!user.isAccountNonLocked()) {
            session.invalidate();
            response.sendRedirect("/auth/login?locked=true");
            return false;
        }
        
        // Admin sayfaları için yetki kontrolü
        if (requestURI.startsWith("/admin/")) {
            if (!user.hasAnyRole("SUPER_ADMIN", "ADMIN")) {
                response.sendRedirect("/?access_denied=true");
                return false;
            }
        }
        
        // Platform yönetimi için yetki kontrolü
        if (requestURI.startsWith("/platform-config/")) {
            if (!user.hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")) {
                response.sendRedirect("/?access_denied=true");
                return false;
            }
        }
        
        return true;
    }
}
