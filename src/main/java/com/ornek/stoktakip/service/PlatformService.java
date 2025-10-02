package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.Platform;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlatformService {
    
    List<Platform> getAllPlatforms();
    
    List<Platform> getActivePlatforms();
    
    List<Platform> getSyncEnabledPlatforms();
    
    Optional<Platform> getPlatformById(Long id);
    
    Optional<Platform> getPlatformByCode(String code);
    
    Platform savePlatform(Platform platform);
    
    void deletePlatform(Long id);
    
    Platform updatePlatform(Platform platform);
    
    List<Platform> getPlatformsNeedingSync();
    
    long countActivePlatforms();
    
    Platform createPlatform(String name, String code, String description);
    
    Platform updatePlatformCredentials(Long platformId, String apiKey, String apiSecret, String accessToken);
    
    boolean testPlatformConnection(Long platformId);
    
    void updateLastSyncTime(Long platformId);
    
    // Dashboard için ek metodlar
    List<Platform> getPlatformsNeedingSync();
    
    long countActivePlatforms();
    
    // Stok güncelleme metodları
    boolean updatePlatformStock(Long platformId);
    
    boolean updateAllPlatformStocks();
    
    Map<String, Object> getPlatformStockUpdates(Long platformId);
    
    // Platform yönetimi metodları
    boolean deletePlatform(Long id);
}
