package com.ornek.stoktakip.service;

public interface CredentialEncryptionService {
    
    /**
     * Metni şifreler
     */
    String encrypt(String plainText);
    
    /**
     * Şifrelenmiş metni çözer
     */
    String decrypt(String encryptedText);
    
    /**
     * Şifreleme anahtarını ayarlar
     */
    void setEncryptionKey(String key);
    
    /**
     * Şifreleme anahtarını alır
     */
    String getEncryptionKey();
}