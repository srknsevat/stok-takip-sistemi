package com.ornek.stoktakip.config;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private UserService userService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<User> userOpt = userService.getUserByUsername(username);
                if (userOpt.isEmpty()) {
                    throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
                }
                
                User user = userOpt.get();
                return org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .accountExpired(!user.isAccountNonExpired())
                        .accountLocked(!user.isAccountNonLocked())
                        .credentialsExpired(!user.isCredentialsNonExpired())
                        .disabled(!user.isEnabled())
                        .build();
            }
        };
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Genel erişim
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Admin sayfaları
                .requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                
                // Platform yönetimi
                .requestMatchers("/platform-config/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                
                // Dashboard
                .requestMatchers("/dashboard/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "OPERATOR")
                
                // Ürün yönetimi
                .requestMatchers("/products/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "OPERATOR")
                
                // Stok hareketleri
                .requestMatchers("/stock-movements/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "OPERATOR")
                
                // API endpoints
                .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "OPERATOR")
                
                // Webhook endpoints (güvenlik için ayrı konfigürasyon gerekebilir)
                .requestMatchers("/webhooks/**").permitAll()
                
                // Diğer tüm istekler için kimlik doğrulama gerekli
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/auth/login?expired=true")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/webhooks/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // H2 Console için
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, 
                org.springframework.security.core.Authentication authentication) -> {
            
            // Session'a kullanıcı bilgilerini ekle
            HttpSession session = request.getSession();
            String username = authentication.getName();
            
            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userRoles", user.getRoles());
                
                // Son giriş zamanını güncelle
                userService.updateLastLogin(username);
            }
            
            // Rol bazlı yönlendirme
            if (userOpt.isPresent() && userOpt.get().hasAnyRole(User.Role.SUPER_ADMIN, User.Role.ADMIN)) {
                response.sendRedirect("/admin/users");
            } else {
                response.sendRedirect("/");
            }
        };
    }
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response, 
                org.springframework.security.core.AuthenticationException exception) -> {
            
            // Başarısız giriş denemesini kaydet
            String username = request.getParameter("username");
            if (username != null) {
                userService.incrementFailedLoginAttempts(username);
            }
            
            response.sendRedirect("/auth/login?error=true");
        };
    }
}