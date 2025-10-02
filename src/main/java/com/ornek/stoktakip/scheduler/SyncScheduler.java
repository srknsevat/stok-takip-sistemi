package com.ornek.stoktakip.scheduler;

import com.ornek.stoktakip.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {
    
    private final StockSyncService stockSyncService;
    
    @Autowired
    public SyncScheduler(StockSyncService stockSyncService) {
        this.stockSyncService = stockSyncService;
    }
    
    /**
     * Her 15 dakikada bir tüm platformları senkronize et
     */
    @Scheduled(cron = "0 */15 * * * *")
    public void syncAllPlatforms() {
        try {
            stockSyncService.syncAllPlatforms();
        } catch (Exception e) {
            // Log error
            System.err.println("Scheduled sync error: " + e.getMessage());
        }
    }
    
    /**
     * Her saatte bir başarısız senkronizasyonları tekrar dene
     */
    @Scheduled(cron = "0 0 * * * *")
    public void retryFailedSyncs() {
        try {
            stockSyncService.retryFailedSyncs();
        } catch (Exception e) {
            // Log error
            System.err.println("Retry failed syncs error: " + e.getMessage());
        }
    }
    
    /**
     * Her gün saat 02:00'da stok tutarlılığını kontrol et
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void checkStockConsistency() {
        try {
            stockSyncService.checkStockConsistency();
        } catch (Exception e) {
            // Log error
            System.err.println("Stock consistency check error: " + e.getMessage());
        }
    }
}
