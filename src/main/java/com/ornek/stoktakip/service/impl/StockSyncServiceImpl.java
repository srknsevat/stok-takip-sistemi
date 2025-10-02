package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.repository.PlatformProductRepository;
import com.ornek.stoktakip.repository.PlatformRepository;
import com.ornek.stoktakip.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StockSyncServiceImpl implements StockSyncService {

    private final PlatformProductRepository platformProductRepository;
    private final PlatformRepository platformRepository;

    @Autowired
    public StockSyncServiceImpl(PlatformProductRepository platformProductRepository,
                               PlatformRepository platformRepository) {
        this.platformProductRepository = platformProductRepository;
        this.platformRepository = platformRepository;
    }

    @Override
    public Integer calculateRealTimeStock(MaterialCard product) {
        try {
            // BOM varsa ATP hesapla, yoksa mevcut stok miktarını kullan
            // Bu kısım ATP service'den gelecek
            return product.getCurrentStock().intValue();
        } catch (Exception e) {
            System.err.println("Gerçek zamanlı stok hesaplama hatası: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean isStockSynced(MaterialCard product) {
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByProductId(product.getId());
            if (platformProducts.isEmpty()) {
                return true; // Platform'da yoksa senkronize sayılır
            }

            for (PlatformProduct platformProduct : platformProducts) {
                if (!platformProduct.getIsSynced()) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Stok senkronizasyon durumu kontrol hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void syncPlatform(Platform platform) {
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platform.getId());
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // Stok miktarını güncelle
                    Integer realTimeStock = calculateRealTimeStock(platformProduct.getProduct());
                    platformProduct.setPlatformStockQuantity(realTimeStock);
                    platformProduct.setIsSynced(true);
                    platformProduct.setLastSyncAt(LocalDateTime.now());
                    platformProduct.setSyncError(null);
                    
                    platformProductRepository.save(platformProduct);
                } catch (Exception e) {
                    platformProduct.setIsSynced(false);
                    platformProduct.setSyncError(e.getMessage());
                    platformProductRepository.save(platformProduct);
                }
            }
            
            // Platform'un son senkronizasyon zamanını güncelle
            platform.setLastSyncAt(LocalDateTime.now());
            platformRepository.save(platform);
            
        } catch (Exception e) {
            System.err.println("Platform senkronizasyon hatası: " + e.getMessage());
        }
    }

    @Override
    public void syncAllPlatforms() {
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            
            for (Platform platform : activePlatforms) {
                syncPlatform(platform);
            }
        } catch (Exception e) {
            System.err.println("Tüm platform senkronizasyon hatası: " + e.getMessage());
        }
    }

    @Override
    public void syncProductOnAllPlatforms(MaterialCard product) {
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByProductId(product.getId());
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // Stok miktarını güncelle
                    Integer realTimeStock = calculateRealTimeStock(product);
                    platformProduct.setPlatformStockQuantity(realTimeStock);
                    platformProduct.setIsSynced(true);
                    platformProduct.setLastSyncAt(LocalDateTime.now());
                    platformProduct.setSyncError(null);
                    
                    platformProductRepository.save(platformProduct);
                } catch (Exception e) {
                    platformProduct.setIsSynced(false);
                    platformProduct.setSyncError(e.getMessage());
                    platformProductRepository.save(platformProduct);
                }
            }
        } catch (Exception e) {
            System.err.println("Ürün senkronizasyon hatası: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getSyncStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            long totalPlatforms = activePlatforms.size();
            long syncedPlatforms = 0;
            long failedPlatforms = 0;
            
            for (Platform platform : activePlatforms) {
                List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platform.getId());
                boolean allSynced = true;
                
                for (PlatformProduct platformProduct : platformProducts) {
                    if (!platformProduct.getIsSynced()) {
                        allSynced = false;
                        break;
                    }
                }
                
                if (allSynced) {
                    syncedPlatforms++;
                } else {
                    failedPlatforms++;
                }
            }
            
            status.put("totalPlatforms", totalPlatforms);
            status.put("syncedPlatforms", syncedPlatforms);
            status.put("failedPlatforms", failedPlatforms);
            status.put("syncRate", totalPlatforms > 0 ? (double) syncedPlatforms / totalPlatforms * 100 : 0);
            
        } catch (Exception e) {
            System.err.println("Senkronizasyon durumu hesaplama hatası: " + e.getMessage());
            status.put("error", e.getMessage());
        }
        
        return status;
    }

    @Override
    public List<Map<String, Object>> getFailedSyncs() {
        List<Map<String, Object>> failedSyncs = new ArrayList<>();
        
        try {
            List<PlatformProduct> unsyncedProducts = platformProductRepository.findByIsSyncedFalse();
            
            for (PlatformProduct platformProduct : unsyncedProducts) {
                Map<String, Object> failedSync = new HashMap<>();
                failedSync.put("id", platformProduct.getId());
                failedSync.put("platformName", platformProduct.getPlatform().getPlatformName());
                failedSync.put("productName", platformProduct.getProduct().getMaterialName());
                failedSync.put("error", platformProduct.getSyncError());
                failedSync.put("lastAttempt", platformProduct.getLastSyncAt());
                failedSyncs.add(failedSync);
            }
        } catch (Exception e) {
            System.err.println("Başarısız senkronizasyonları getirme hatası: " + e.getMessage());
        }
        
        return failedSyncs;
    }

    @Override
    public List<Map<String, Object>> getPlatformSyncStatus() {
        List<Map<String, Object>> platformStatus = new ArrayList<>();
        
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            
            for (Platform platform : activePlatforms) {
                Map<String, Object> status = new HashMap<>();
                status.put("platformId", platform.getId());
                status.put("platformName", platform.getPlatformName());
                status.put("platformCode", platform.getPlatformCode());
                
                List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platform.getId());
                long totalProducts = platformProducts.size();
                long syncedProducts = platformProducts.stream()
                    .mapToLong(pp -> pp.getIsSynced() ? 1 : 0)
                    .sum();
                
                status.put("totalProducts", totalProducts);
                status.put("syncedProducts", syncedProducts);
                status.put("failedProducts", totalProducts - syncedProducts);
                status.put("syncRate", totalProducts > 0 ? (double) syncedProducts / totalProducts * 100 : 0);
                status.put("lastSyncAt", platform.getLastSyncAt());
                
                platformStatus.add(status);
            }
        } catch (Exception e) {
            System.err.println("Platform senkronizasyon durumu hesaplama hatası: " + e.getMessage());
        }
        
        return platformStatus;
    }

    @Override
    public List<Map<String, Object>> getSyncHistory(Long platformId) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platformId);
            
            for (PlatformProduct platformProduct : platformProducts) {
                Map<String, Object> record = new HashMap<>();
                record.put("productId", platformProduct.getProduct().getId());
                record.put("productName", platformProduct.getProduct().getMaterialName());
                record.put("isSynced", platformProduct.getIsSynced());
                record.put("lastSyncAt", platformProduct.getLastSyncAt());
                record.put("syncError", platformProduct.getSyncError());
                history.add(record);
            }
        } catch (Exception e) {
            System.err.println("Senkronizasyon geçmişi getirme hatası: " + e.getMessage());
        }
        
        return history;
    }

    @Override
    public Map<String, Object> getSyncStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            List<Platform> activePlatforms = platformRepository.findByIsActiveTrue();
            long totalPlatforms = activePlatforms.size();
            long totalProducts = 0;
            long syncedProducts = 0;
            long failedProducts = 0;
            
            for (Platform platform : activePlatforms) {
                List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platform.getId());
                totalProducts += platformProducts.size();
                
                for (PlatformProduct platformProduct : platformProducts) {
                    if (platformProduct.getIsSynced()) {
                        syncedProducts++;
                    } else {
                        failedProducts++;
                    }
                }
            }
            
            statistics.put("totalPlatforms", totalPlatforms);
            statistics.put("totalProducts", totalProducts);
            statistics.put("syncedProducts", syncedProducts);
            statistics.put("failedProducts", failedProducts);
            statistics.put("overallSyncRate", totalProducts > 0 ? (double) syncedProducts / totalProducts * 100 : 0);
            
        } catch (Exception e) {
            System.err.println("Senkronizasyon istatistikleri hesaplama hatası: " + e.getMessage());
            statistics.put("error", e.getMessage());
        }
        
        return statistics;
    }
}