package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.*;
import com.ornek.stoktakip.integration.PlatformIntegrationService;
import com.ornek.stoktakip.repository.*;
import com.ornek.stoktakip.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class StockSyncServiceImpl implements StockSyncService {
    
    private final PlatformRepository platformRepository;
    private final PlatformProductRepository platformProductRepository;
    private final ProductRepository productRepository;
    private final SyncLogRepository syncLogRepository;
    private final OrderItemRepository orderItemRepository;
    private final Map<String, PlatformIntegrationService> integrationServices;
    private final ExecutorService executorService;
    
    @Autowired
    public StockSyncServiceImpl(PlatformRepository platformRepository,
                               PlatformProductRepository platformProductRepository,
                               ProductRepository productRepository,
                               SyncLogRepository syncLogRepository,
                               OrderItemRepository orderItemRepository,
                               Map<String, PlatformIntegrationService> integrationServices) {
        this.platformRepository = platformRepository;
        this.platformProductRepository = platformProductRepository;
        this.productRepository = productRepository;
        this.syncLogRepository = syncLogRepository;
        this.orderItemRepository = orderItemRepository;
        this.integrationServices = integrationServices;
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    @Override
    public void syncAllPlatforms() {
        List<Platform> activePlatforms = platformRepository.findActiveAndSyncEnabledPlatforms();
        
        for (Platform platform : activePlatforms) {
            CompletableFuture.runAsync(() -> syncPlatform(platform), executorService);
        }
    }
    
    @Override
    public void syncPlatform(Platform platform) {
        try {
            // Platform bağlantısını test et
            if (!testPlatformConnection(platform)) {
                logSyncError(platform, null, SyncLog.SyncType.FULL_SYNC, 
                    "Platform bağlantısı başarısız: " + platform.getName());
                return;
            }
            
            // Platform'daki tüm ürünleri getir
            List<PlatformProduct> platformProducts = platformProductRepository.findByPlatform(platform);
            
            for (PlatformProduct platformProduct : platformProducts) {
                if (platformProduct.getIsActive() && platformProduct.getProduct().getSyncEnabled()) {
                    syncProductOnPlatform(platform, platformProduct);
                }
            }
            
            // Platform'ın son senkronizasyon zamanını güncelle
            platform.setLastSyncAt(LocalDateTime.now());
            platformRepository.save(platform);
            
            logSyncSuccess(platform, null, SyncLog.SyncType.FULL_SYNC, 
                "Platform senkronizasyonu tamamlandı: " + platform.getName());
                
        } catch (Exception e) {
            logSyncError(platform, null, SyncLog.SyncType.FULL_SYNC, 
                "Platform senkronizasyon hatası: " + e.getMessage());
        }
    }
    
    @Override
    public void syncProduct(Product product) {
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductAndIsActiveTrue(product);
        
        for (PlatformProduct platformProduct : platformProducts) {
            Platform platform = platformProduct.getPlatform();
            if (platform.getIsActive() && platform.getSyncEnabled()) {
                CompletableFuture.runAsync(() -> syncProductOnPlatform(platform, platformProduct), executorService);
            }
        }
    }
    
    @Override
    public void updateStockAfterSale(Product product, Integer soldQuantity) {
        // Ana stoktan düş
        Integer currentStock = product.getStockQuantity();
        Integer newStock = currentStock - soldQuantity;
        
        if (newStock < 0) {
            throw new RuntimeException("Yetersiz stok: " + product.getName());
        }
        
        product.setStockQuantity(newStock);
        productRepository.save(product);
        
        // Tüm platformlarda stok güncelle
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductAndIsActiveTrue(product);
        
        for (PlatformProduct platformProduct : platformProducts) {
            Platform platform = platformProduct.getPlatform();
            if (platform.getIsActive() && platform.getSyncEnabled()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        updateStockOnPlatform(platform, product, newStock);
                        platformProduct.setPlatformStockQuantity(newStock);
                        platformProduct.setIsSynced(true);
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                    } catch (Exception e) {
                        logSyncError(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                            "Stok güncelleme hatası: " + e.getMessage());
                    }
                }, executorService);
            }
        }
    }
    
    @Override
    public void restoreStockAfterReturn(Product product, Integer returnedQuantity) {
        // Ana stoka ekle
        Integer currentStock = product.getStockQuantity();
        Integer newStock = currentStock + returnedQuantity;
        
        product.setStockQuantity(newStock);
        productRepository.save(product);
        
        // Tüm platformlarda stok güncelle
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductAndIsActiveTrue(product);
        
        for (PlatformProduct platformProduct : platformProducts) {
            Platform platform = platformProduct.getPlatform();
            if (platform.getIsActive() && platform.getSyncEnabled()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        updateStockOnPlatform(platform, product, newStock);
                        platformProduct.setPlatformStockQuantity(newStock);
                        platformProduct.setIsSynced(true);
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                    } catch (Exception e) {
                        logSyncError(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                            "Stok geri yükleme hatası: " + e.getMessage());
                    }
                }, executorService);
            }
        }
    }
    
    @Override
    public void updateStockManually(Product product, Integer newStockQuantity) {
        // Ana stoku güncelle
        product.setStockQuantity(newStockQuantity);
        productRepository.save(product);
        
        // Tüm platformlarda stok güncelle
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductAndIsActiveTrue(product);
        
        for (PlatformProduct platformProduct : platformProducts) {
            Platform platform = platformProduct.getPlatform();
            if (platform.getIsActive() && platform.getSyncEnabled()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        updateStockOnPlatform(platform, product, newStockQuantity);
                        platformProduct.setPlatformStockQuantity(newStockQuantity);
                        platformProduct.setIsSynced(true);
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                    } catch (Exception e) {
                        logSyncError(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                            "Manuel stok güncelleme hatası: " + e.getMessage());
                    }
                }, executorService);
            }
        }
    }
    
    @Override
    public void retryFailedSyncs() {
        List<SyncLog> failedSyncs = syncLogRepository.findRetryableSyncs(LocalDateTime.now());
        
        for (SyncLog syncLog : failedSyncs) {
            if (syncLog.canRetry()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        Platform platform = syncLog.getPlatform();
                        Product product = syncLog.getProduct();
                        
                        if (product != null) {
                            syncProduct(product);
                        } else {
                            syncPlatform(platform);
                        }
                    } catch (Exception e) {
                        logSyncError(syncLog.getPlatform(), syncLog.getProduct(), 
                            syncLog.getSyncType(), "Retry hatası: " + e.getMessage());
                    }
                }, executorService);
            }
        }
    }
    
    @Override
    public void checkStockConsistency() {
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            if (product.getSyncEnabled()) {
                Integer realTimeStock = calculateRealTimeStock(product);
                
                if (!realTimeStock.equals(product.getStockQuantity())) {
                    // Stok tutarsızlığı tespit edildi
                    product.setStockQuantity(realTimeStock);
                    productRepository.save(product);
                    
                    // Platformlarda güncelle
                    syncProduct(product);
                }
            }
        }
    }
    
    @Override
    public Integer calculateRealTimeStock(Product product) {
        // Başlangıç stoku
        Integer baseStock = product.getStockQuantity();
        
        // İşlenmemiş siparişlerden düşülen stok
        List<OrderItem> unprocessedItems = orderItemRepository.findUnprocessedByProduct(product);
        Integer reservedStock = unprocessedItems.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
        
        // Gerçek zamanlı stok = Ana stok - Rezerve edilen stok
        return baseStock - reservedStock;
    }
    
    @Override
    public boolean isStockSynced(Product product) {
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductAndIsActiveTrue(product);
        
        for (PlatformProduct platformProduct : platformProducts) {
            if (!platformProduct.getIsSynced()) {
                return false;
            }
            
            if (!product.getStockQuantity().equals(platformProduct.getPlatformStockQuantity())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean updateStockOnPlatform(Platform platform, Product product, Integer newStockQuantity) {
        try {
            PlatformIntegrationService integrationService = getIntegrationService(platform);
            
            if (integrationService != null) {
                boolean success = integrationService.updateStock(platform, product, newStockQuantity);
                
                if (success) {
                    logSyncSuccess(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                        "Stok güncellendi: " + product.getName());
                } else {
                    logSyncError(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                        "Stok güncelleme başarısız: " + product.getName());
                }
                
                return success;
            }
            
            return false;
            
        } catch (Exception e) {
            logSyncError(platform, product, SyncLog.SyncType.STOCK_UPDATE, 
                "Platform stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    private void syncProductOnPlatform(Platform platform, PlatformProduct platformProduct) {
        try {
            PlatformIntegrationService integrationService = getIntegrationService(platform);
            
            if (integrationService != null) {
                Product product = platformProduct.getProduct();
                
                // Platform'da ürün güncelle
                PlatformProduct updatedPlatformProduct = integrationService.updateProduct(
                    platform, product, platformProduct);
                
                if (updatedPlatformProduct != null) {
                    platformProduct.setPlatformTitle(product.getName());
                    platformProduct.setPlatformPrice(product.getPrice());
                    platformProduct.setPlatformStockQuantity(product.getStockQuantity());
                    platformProduct.setIsSynced(true);
                    platformProduct.setLastSyncAt(LocalDateTime.now());
                    platformProduct.setSyncErrorMessage(null);
                    platformProduct.setRetryCount(0);
                    
                    platformProductRepository.save(platformProduct);
                    
                    logSyncSuccess(platform, product, SyncLog.SyncType.PRODUCT_UPDATE, 
                        "Ürün senkronize edildi: " + product.getName());
                }
            }
            
        } catch (Exception e) {
            platformProduct.setIsSynced(false);
            platformProduct.setSyncErrorMessage(e.getMessage());
            platformProduct.setRetryCount(platformProduct.getRetryCount() + 1);
            platformProductRepository.save(platformProduct);
            
            logSyncError(platform, platformProduct.getProduct(), SyncLog.SyncType.PRODUCT_UPDATE, 
                "Ürün senkronizasyon hatası: " + e.getMessage());
        }
    }
    
    private PlatformIntegrationService getIntegrationService(Platform platform) {
        String serviceName = platform.getCode().toLowerCase() + "IntegrationService";
        return integrationServices.get(serviceName);
    }
    
    private boolean testPlatformConnection(Platform platform) {
        try {
            PlatformIntegrationService integrationService = getIntegrationService(platform);
            return integrationService != null && integrationService.testConnection(platform);
        } catch (Exception e) {
            return false;
        }
    }
    
    private void logSyncSuccess(Platform platform, Product product, SyncLog.SyncType syncType, String message) {
        SyncLog syncLog = new SyncLog(platform, syncType, SyncLog.SyncStatus.SUCCESS);
        syncLog.setProduct(product);
        syncLog.setSyncMessage(message);
        syncLog.setExecutionTimeMs(System.currentTimeMillis());
        syncLogRepository.save(syncLog);
    }
    
    private void logSyncError(Platform platform, Product product, SyncLog.SyncType syncType, String errorMessage) {
        SyncLog syncLog = new SyncLog(platform, syncType, SyncLog.SyncStatus.FAILED);
        syncLog.setProduct(product);
        syncLog.setErrorMessage(errorMessage);
        syncLog.setExecutionTimeMs(System.currentTimeMillis());
        syncLogRepository.save(syncLog);
    }
}
