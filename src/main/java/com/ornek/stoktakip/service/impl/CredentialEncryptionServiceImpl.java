package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.service.CredentialEncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CredentialEncryptionServiceImpl implements CredentialEncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    @Value("${app.encryption.key:defaultEncryptionKey123456789012345678901234567890}")
    private String encryptionKey;
    
    private SecretKey secretKey;
    
    public CredentialEncryptionServiceImpl() {
        initializeSecretKey();
    }
    
    private void initializeSecretKey() {
        try {
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            // AES-256 için 32 byte gerekli
            byte[] key = new byte[32];
            System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
            this.secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            System.err.println("Şifreleme anahtarı oluşturulurken hata: " + e.getMessage());
            // Varsayılan anahtar oluştur
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
                keyGenerator.init(256);
                this.secretKey = keyGenerator.generateKey();
            } catch (Exception ex) {
                System.err.println("Varsayılan şifreleme anahtarı oluşturulamadı: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Şifreleme hatası: " + e.getMessage());
            return plainText; // Hata durumunda orijinal metni döndür
        }
    }
    
    @Override
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Şifre çözme hatası: " + e.getMessage());
            return encryptedText; // Hata durumunda şifrelenmiş metni döndür
        }
    }
    
    @Override
    public void setEncryptionKey(String key) {
        this.encryptionKey = key;
        initializeSecretKey();
    }
    
    @Override
    public String getEncryptionKey() {
        return this.encryptionKey;
    }
}
