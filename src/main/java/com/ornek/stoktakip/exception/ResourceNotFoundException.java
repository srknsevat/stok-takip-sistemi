package com.ornek.stoktakip.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " bulunamadı: " + id);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " bulunamadı: " + identifier);
    }
}
