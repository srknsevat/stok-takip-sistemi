package com.ornek.stoktakip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
public class IntegrationConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // HTTP Client Factory
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 saniye
        factory.setReadTimeout(30000);    // 30 saniye
        restTemplate.setRequestFactory(factory);
        
        // Message Converters
        restTemplate.setMessageConverters(Arrays.asList(
            new MappingJackson2HttpMessageConverter(),
            new StringHttpMessageConverter(StandardCharsets.UTF_8),
            new FormHttpMessageConverter()
        ));
        
        return restTemplate;
    }
}
