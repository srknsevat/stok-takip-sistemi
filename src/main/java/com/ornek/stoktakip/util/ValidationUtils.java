package com.ornek.stoktakip.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s()]+$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isAlphanumeric(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        return ALPHANUMERIC_PATTERN.matcher(text).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;
        return true;
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        if (username.length() < 3 || username.length() > 50) return false;
        return isAlphanumeric(username);
    }
    
    public static boolean isValidMaterialCode(String materialCode) {
        if (materialCode == null || materialCode.trim().isEmpty()) return false;
        if (materialCode.length() < 3 || materialCode.length() > 50) return false;
        return isAlphanumeric(materialCode);
    }
    
    public static boolean isValidQuantity(String quantity) {
        if (quantity == null || quantity.trim().isEmpty()) return false;
        try {
            double qty = Double.parseDouble(quantity);
            return qty > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidPrice(String price) {
        if (price == null || price.trim().isEmpty()) return false;
        try {
            double p = Double.parseDouble(price);
            return p >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("[<>\"'&]", "");
    }
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
}
