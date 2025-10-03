package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.repository.PlatformRepository;
import com.ornek.stoktakip.repository.PlatformProductRepository;
import com.ornek.stoktakip.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class StockSyncServiceImpl implements StockSyncService {
    
    private final PlatformRepository platformRepository;
    private final PlatformProductRepository platformProductRepository;
    
    @Autowired
    public StockSyncServiceImpl(PlatformRepository platformRepository, 
                               PlatformProductRepository platformProductRepository) {
        this.platformRepository = platformRepository;
        this.platformProductRepository = platformProductRepository;
    }
    
    @Override
    public boolean syncAllPlatforms() {
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            boolean allSuccess = true;
            
            for (Platform platform : activePlatforms) {
                if (!syncPlatform(platform.getId())) {
                    allSuccess = false;
                }
            }
            
            return allSuccess;
        } catch (Exception e) {
            System.err.println("Tüm platformlar senkronize edilirken hata: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean syncPlatform(Long platformId) {
        try {
            Platform platform = platformRepository.findById(platformId).orElse(null);
            if (platform == null) {
                return false;
            }
            
            List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platformId);
            int successCount = 0;
            int totalCount = platformProducts.size();
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // Bu kısım gerçek API çağrıları ile doldurulacak
                    // Şimdilik basit bir güncelleme
                    platformProduct.setLastSyncAt(LocalDateTime.now());
                    platformProductRepository.save(platformProduct);
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Platform ürün senkronizasyon hatası: " + e.getMessage());
                }
            }
            
            // Platform son senkronizasyon zamanını güncelle
            platform.setLastSyncAt(LocalDateTime.now());
            platformRepository.save(platform);
            
            return successCount == totalCount;
            
        } catch (Exception e) {
            System.err.println("Platform senkronizasyon hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean syncMaterialStock(Long materialId) {
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByProductId(materialId);
            boolean allSuccess = true;
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // Bu kısım gerçek API çağrıları ile doldurulacak
                    platformProduct.setLastSyncAt(LocalDateTime.now());
                    platformProductRepository.save(platformProduct);
                } catch (Exception e) {
                    System.err.println("Malzeme stok senkronizasyon hatası: " + e.getMessage());
                    allSuccess = false;
                }
            }
            
            return allSuccess;
            
        } catch (Exception e) {
            System.err.println("Malzeme stok senkronizasyon hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updatePlatformStock(PlatformProduct platformProduct, Integer newStock) {
        try {
            platformProduct.setPlatformStockQuantity(newStock);
            platformProduct.setLastSyncAt(LocalDateTime.now());
            platformProductRepository.save(platformProduct);
            return true;
        } catch (Exception e) {
            System.err.println("Platform stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isSyncNeeded(Platform platform) {
        if (platform.getLastSyncAt() == null) {
            return true;
        }
        
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return platform.getLastSyncAt().isBefore(oneHourAgo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSyncHistory() {
        List<Map<String, Object>> history = new ArrayList<>();
        
        try {
            List<Platform> platforms = platformRepository.findAll();
            for (Platform platform : platforms) {
                Map<String, Object> platformHistory = new HashMap<>();
                platformHistory.put("platformId", platform.getId());
                platformHistory.put("platformName", platform.getPlatformName());
                platformHistory.put("lastSyncAt", platform.getLastSyncAt());
                platformHistory.put("isActive", platform.getIsActive());
                platformHistory.put("syncEnabled", platform.getSyncEnabled());
                history.add(platformHistory);
            }
        } catch (Exception e) {
            System.err.println("Senkronizasyon geçmişi alınırken hata: " + e.getMessage());
        }
        
        return history;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSyncStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalPlatforms = platformRepository.count();
            long activePlatforms = platformRepository.countActivePlatforms();
            long syncEnabledPlatforms = platformRepository.countActiveAndSyncEnabled();
            
            stats.put("totalPlatforms", totalPlatforms);
            stats.put("activePlatforms", activePlatforms);
            stats.put("syncEnabledPlatforms", syncEnabledPlatforms);
            stats.put("lastSyncTime", LocalDateTime.now());
            
        } catch (Exception e) {
            System.err.println("Senkronizasyon istatistikleri alınırken hata: " + e.getMessage());
        }
        
        return stats;
    }
    
    @Override
    public boolean retryFailedSync(Long platformId) {
        return syncPlatform(platformId);
    }
    
    @Override
    public void stopSync() {
        // Senkronizasyonu durdurma işlemi
        System.out.println("Senkronizasyon durduruldu");
    }
    
    @Override
    public void startSync() {
        // Senkronizasyonu başlatma işlemi
        System.out.println("Senkronizasyon başlatıldı");
    }
    
    @Override
    public void retryFailedSyncs() {
        // Başarısız senkronizasyonları tekrar dene
        List<PlatformProduct> failedProducts = platformProductRepository.findByIsSyncedFalse();
        for (PlatformProduct product : failedProducts) {
            try {
                syncPlatform(product.getPlatform().getId());
            } catch (Exception e) {
                System.err.println("Senkronizasyon hatası: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void checkStockConsistency() {
        // Stok tutarlılığını kontrol et
        List<PlatformProduct> allProducts = platformProductRepository.findAll();
        for (PlatformProduct product : allProducts) {
            // Stok tutarlılığı kontrolü
            if (product.getPlatformStockQuantity() != product.getProduct().getStockQuantity()) {
                System.out.println("Stok tutarsızlığı tespit edildi: " + product.getProduct().getName());
            }
        }
    }
}