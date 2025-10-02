package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.repository.PlatformRepository;
import com.ornek.stoktakip.service.PlatformService;
import com.ornek.stoktakip.service.CredentialEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PlatformServiceImpl implements PlatformService {
    
    private final PlatformRepository platformRepository;
    private final CredentialEncryptionService encryptionService;
    
    @Autowired
    public PlatformServiceImpl(PlatformRepository platformRepository, 
                             CredentialEncryptionService encryptionService) {
        this.platformRepository = platformRepository;
        this.encryptionService = encryptionService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Platform> getActivePlatforms() {
        return platformRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Platform> getSyncEnabledPlatforms() {
        return platformRepository.findBySyncEnabledTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Platform> getPlatformById(Long id) {
        return platformRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Platform> getPlatformByCode(String code) {
        return platformRepository.findByCode(code);
    }
    
    @Override
    public Platform savePlatform(Platform platform) {
        return platformRepository.save(platform);
    }
    
    @Override
    public void deletePlatform(Long id) {
        platformRepository.deleteById(id);
    }
    
    @Override
    public Platform updatePlatform(Platform platform) {
        return platformRepository.save(platform);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Platform> getPlatformsNeedingSync() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return platformRepository.findPlatformsNeedingSync(oneHourAgo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActivePlatforms() {
        return platformRepository.countActivePlatforms();
    }
    
    @Override
    public Platform createPlatform(String name, String code, String description) {
        Platform platform = new Platform(name, code, description);
        return platformRepository.save(platform);
    }
    
    @Override
    public Platform updatePlatformCredentials(Long platformId, String apiKey, String apiSecret, String accessToken) {
        return platformRepository.findById(platformId)
                .map(platform -> {
                    // Kimlik bilgilerini şifreleyerek sakla
                    platform.setApiKey(encryptionService.encrypt(apiKey));
                    platform.setApiSecret(encryptionService.encrypt(apiSecret));
                    if (accessToken != null && !accessToken.isEmpty()) {
                        platform.setAccessToken(encryptionService.encrypt(accessToken));
                    }
                    return platformRepository.save(platform);
                })
                .orElseThrow(() -> new RuntimeException("Platform bulunamadı: " + platformId));
    }
    
    /**
     * Platform'un şifrelenmiş kimlik bilgilerini çözer
     */
    public Platform getPlatformWithDecryptedCredentials(Long platformId) {
        return platformRepository.findById(platformId)
                .map(platform -> {
                    // Şifrelenmiş kimlik bilgilerini çöz
                    if (platform.getApiKey() != null) {
                        platform.setApiKey(encryptionService.decrypt(platform.getApiKey()));
                    }
                    if (platform.getApiSecret() != null) {
                        platform.setApiSecret(encryptionService.decrypt(platform.getApiSecret()));
                    }
                    if (platform.getAccessToken() != null) {
                        platform.setAccessToken(encryptionService.decrypt(platform.getAccessToken()));
                    }
                    return platform;
                })
                .orElse(null);
    }
    
    @Override
    public boolean testPlatformConnection(Long platformId) {
        return platformRepository.findById(platformId)
                .map(platform -> {
                    // Burada gerçek API bağlantı testi yapılacak
                    // Şimdilik basit bir kontrol
                    return platform.getApiKey() != null && !platform.getApiKey().isEmpty();
                })
                .orElse(false);
    }
    
    @Override
    public void updateLastSyncTime(Long platformId) {
        platformRepository.findById(platformId)
                .ifPresent(platform -> {
                    platform.setLastSyncAt(LocalDateTime.now());
                    platformRepository.save(platform);
                });
    }
    
    // Dashboard için ek metodlar
    @Override
    public boolean updatePlatformStock(Long platformId) {
        try {
            Platform platform = platformRepository.findById(platformId).orElse(null);
            if (platform == null) {
                return false;
            }
            
            // Bu kısım StockSyncService'den gelecek
            // Şimdilik basit bir güncelleme
            platform.setLastSyncAt(LocalDateTime.now());
            platformRepository.save(platform);
            
            return true;
        } catch (Exception e) {
            System.err.println("Platform stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updateAllPlatformStocks() {
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            boolean allSuccess = true;
            
            for (Platform platform : activePlatforms) {
                if (!updatePlatformStock(platform.getId())) {
                    allSuccess = false;
                }
            }
            
            return allSuccess;
        } catch (Exception e) {
            System.err.println("Tüm platform stokları güncellenirken hata: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getPlatformStockUpdates(Long platformId) {
        Map<String, Object> updates = new HashMap<>();
        
        try {
            Platform platform = platformRepository.findById(platformId).orElse(null);
            if (platform == null) {
                updates.put("error", "Platform bulunamadı");
                return updates;
            }
            
            updates.put("platformId", platformId);
            updates.put("platformName", platform.getPlatformName());
            updates.put("lastSyncAt", platform.getLastSyncAt());
            updates.put("syncEnabled", platform.getSyncEnabled());
            updates.put("isActive", platform.getIsActive());
            
        } catch (Exception e) {
            updates.put("error", e.getMessage());
        }
        
        return updates;
    }
}
