package com.ornek.stoktakip.config;

import com.ornek.stoktakip.interceptor.LoggingInterceptor;
import com.ornek.stoktakip.interceptor.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoggingInterceptor loggingInterceptor;
    
    @Autowired
    private SecurityInterceptor securityInterceptor;
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Ana sayfa yönlendirmesi
        registry.addRedirectViewController("/", "/dashboard");
        
        // Login sayfası
        registry.addViewController("/login").setViewName("auth/login");
        
        // Dashboard sayfası
        registry.addViewController("/dashboard").setViewName("dashboard/index");
        
        // Hata sayfaları
        registry.addViewController("/error/403").setViewName("error/403");
        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/500").setViewName("error/500");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico");
        
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/h2-console/**");
    }
}