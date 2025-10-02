package com.ornek.stoktakip.constants;

public class SystemConstants {
    
    // System Information
    public static final String SYSTEM_NAME = "Stok Takip Sistemi";
    public static final String SYSTEM_VERSION = "1.0.0";
    public static final String SYSTEM_DESCRIPTION = "Entegre E-ticaret Stok Yönetim Sistemi";
    
    // Default Values
    public static final String DEFAULT_CURRENCY = "TRY";
    public static final String DEFAULT_UNIT = "ADET";
    public static final String DEFAULT_LANGUAGE = "tr";
    public static final String DEFAULT_TIMEZONE = "Europe/Istanbul";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 5;
    
    // Cache
    public static final String CACHE_PLATFORMS = "platforms";
    public static final String CACHE_MATERIALS = "materials";
    public static final String CACHE_USERS = "users";
    public static final String CACHE_ORDERS = "orders";
    public static final String CACHE_BOMS = "boms";
    
    // Cache TTL (Time To Live) in seconds
    public static final int CACHE_TTL_SHORT = 300; // 5 minutes
    public static final int CACHE_TTL_MEDIUM = 1800; // 30 minutes
    public static final int CACHE_TTL_LONG = 3600; // 1 hour
    
    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {"jpg", "jpeg", "png", "gif", "pdf", "xlsx", "csv"};
    
    // Security
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int SESSION_TIMEOUT = 1800; // 30 minutes
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    
    // Business Rules
    public static final int MIN_STOCK_LEVEL = 0;
    public static final int MAX_STOCK_LEVEL = 999999;
    public static final int REORDER_POINT_DEFAULT = 10;
    public static final int REORDER_QUANTITY_DEFAULT = 50;
    
    // Date Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String TIME_FORMAT = "HH:mm";
    
    // Status Values
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    // Error Messages
    public static final String ERROR_REQUIRED_FIELD = "Bu alan zorunludur";
    public static final String ERROR_INVALID_EMAIL = "Geçerli bir e-posta adresi giriniz";
    public static final String ERROR_INVALID_PHONE = "Geçerli bir telefon numarası giriniz";
    public static final String ERROR_INVALID_PASSWORD = "Şifre en az 6 karakter olmalıdır";
    public static final String ERROR_INVALID_QUANTITY = "Geçerli bir miktar giriniz";
    public static final String ERROR_INVALID_PRICE = "Geçerli bir fiyat giriniz";
    
    // Success Messages
    public static final String SUCCESS_SAVED = "Kayıt başarıyla kaydedildi";
    public static final String SUCCESS_UPDATED = "Kayıt başarıyla güncellendi";
    public static final String SUCCESS_DELETED = "Kayıt başarıyla silindi";
    public static final String SUCCESS_CREATED = "Kayıt başarıyla oluşturuldu";
    
    // Platform Codes
    public static final String PLATFORM_EBAY = "EBAY";
    public static final String PLATFORM_SHOPIFY = "SHOPIFY";
    public static final String PLATFORM_AMAZON = "AMAZON";
    public static final String PLATFORM_TRENDYOL = "TRENDYOL";
    
    // Material Types
    public static final String MATERIAL_TYPE_RAW = "RAW_MATERIAL";
    public static final String MATERIAL_TYPE_COMPONENT = "COMPONENT";
    public static final String MATERIAL_TYPE_FINISHED = "FINISHED_PRODUCT";
    public static final String MATERIAL_TYPE_SEMI_FINISHED = "SEMI_FINISHED";
    
    // Order Status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    public static final String ORDER_STATUS_RETURNED = "RETURNED";
    
    // User Roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_VIEWER = "VIEWER";
}
