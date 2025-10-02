package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.MaterialCard;
import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.repository.MaterialCardRepository;
import com.ornek.stoktakip.repository.PlatformProductRepository;
import com.ornek.stoktakip.repository.PlatformRepository;
import com.ornek.stoktakip.service.ATPService;
import com.ornek.stoktakip.service.BomExplosionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ATPServiceImpl implements ATPService {
    
    private final MaterialCardRepository materialCardRepository;
    private final PlatformProductRepository platformProductRepository;
    private final PlatformRepository platformRepository;
    private final BomExplosionService bomExplosionService;
    
    @Autowired
    public ATPServiceImpl(MaterialCardRepository materialCardRepository,
                         PlatformProductRepository platformProductRepository,
                         PlatformRepository platformRepository,
                         BomExplosionService bomExplosionService) {
        this.materialCardRepository = materialCardRepository;
        this.platformProductRepository = platformProductRepository;
        this.platformRepository = platformRepository;
        this.bomExplosionService = bomExplosionService;
    }
    
    @Override
    public ATPResult calculateATP(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
        if (material == null) {
            return null;
        }
        
        // Eğer malzemenin BOM'u varsa, BOM'dan hesapla
        if (hasBOM(materialId)) {
            return calculateATPFromBOM(materialId);
        }
        
        // BOM yoksa, mevcut stok miktarını kullan
        ATPResult result = new ATPResult(material, material.getCurrentStock(), material.getCurrentStock());
        result.setCalculationMethod("DIRECT_STOCK");
        result.setSafetyStock(material.getMinStockLevel());
        result.setReorderPoint(material.getReorderPoint());
        result.setReorderQuantity(material.getReorderQuantity());
        
        return result;
    }
    
    @Override
    public ATPResult calculateATPFromBOM(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
        if (material == null) {
            return null;
        }
        
        // BOM'u patlat ve tüm alt parçaları hesapla
        Map<String, BomExplosionService.BomExplosionResult> explosionResults = 
            bomExplosionService.explodeBOM(materialId, BigDecimal.ONE);
        
        if (explosionResults.isEmpty()) {
            // BOM yoksa, mevcut stok miktarını kullan
            return calculateATP(materialId);
        }
        
        // En kısıtlayıcı alt parçayı bul
        BigDecimal minPossibleQuantity = null;
        List<ATPConstraint> constraints = new ArrayList<>();
        
        for (BomExplosionService.BomExplosionResult explosionResult : explosionResults.values()) {
            MaterialCard childMaterial = explosionResult.getMaterial();
            BigDecimal requiredQuantity = explosionResult.getRequiredQuantity();
            BigDecimal availableQuantity = childMaterial.getCurrentStock();
            
            // Bu alt parçadan kaç adet üretilebilir?
            BigDecimal possibleQuantity = availableQuantity.divide(requiredQuantity, 0, BigDecimal.ROUND_DOWN);
            
            // En kısıtlayıcı miktarı bul
            if (minPossibleQuantity == null || possibleQuantity.compareTo(minPossibleQuantity) < 0) {
                minPossibleQuantity = possibleQuantity;
            }
            
            // Kısıtlama oluştur
            ATPConstraint constraint = new ATPConstraint(childMaterial, requiredQuantity, availableQuantity);
            constraint.setBomLevel(explosionResult.getBomLevel());
            constraint.setBomPath(explosionResult.getBomPath());
            constraint.setConstraintType("STOCK");
            constraint.setDescription(childMaterial.getMaterialName() + " stok kısıtlaması");
            
            constraints.add(constraint);
        }
        
        // ATP sonucu oluştur
        ATPResult result = new ATPResult(material, material.getCurrentStock(), minPossibleQuantity);
        result.setCalculationMethod("BOM_BASED");
        result.setConstraints(constraints);
        result.setSafetyStock(material.getMinStockLevel());
        result.setReorderPoint(material.getReorderPoint());
        result.setReorderQuantity(material.getReorderQuantity());
        
        return result;
    }
    
    @Override
    public boolean updateAllPlatformStocks() {
        try {
            List<Platform> platforms = platformRepository.findByIsActiveTrue();
            boolean allSuccess = true;
            
            for (Platform platform : platforms) {
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
    public boolean updatePlatformStock(Long platformId) {
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
                    // ATP hesapla
                    ATPResult atpResult = calculateATP(platformProduct.getProduct().getId());
                    if (atpResult != null) {
                        // Platform stok miktarını güncelle
                        platformProduct.setPlatformStockQuantity(atpResult.getAvailableToPromise().intValue());
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                        successCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Platform stok güncelleme hatası: " + e.getMessage());
                }
            }
            
            return successCount == totalCount;
            
        } catch (Exception e) {
            System.err.println("Platform stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updateMaterialStockOnAllPlatforms(Long materialId) {
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByProductId(materialId);
            boolean allSuccess = true;
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // ATP hesapla
                    ATPResult atpResult = calculateATP(materialId);
                    if (atpResult != null) {
                        // Platform stok miktarını güncelle
                        platformProduct.setPlatformStockQuantity(atpResult.getAvailableToPromise().intValue());
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                    }
                } catch (Exception e) {
                    System.err.println("Malzeme stok güncelleme hatası: " + e.getMessage());
                    allSuccess = false;
                }
            }
            
            return allSuccess;
            
        } catch (Exception e) {
            System.err.println("Malzeme stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public ATPReport generateATPReport(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
        if (material == null) {
            return null;
        }
        
        ATPReport report = new ATPReport();
        report.setMaterial(material);
        report.setReportDate(LocalDateTime.now());
        
        // ATP hesapla
        ATPResult atpResult = calculateATP(materialId);
        report.setAtpResult(atpResult);
        
        // Kısıtlamaları ekle
        if (atpResult != null && atpResult.hasConstraints()) {
            report.setConstraints(atpResult.getConstraints());
        }
        
        // Platform stok bilgilerini ekle
        List<PlatformProduct> platformProducts = platformProductRepository.findByProductId(materialId);
        List<PlatformStockInfo> platformStocks = new ArrayList<>();
        
        for (PlatformProduct platformProduct : platformProducts) {
            PlatformStockInfo stockInfo = new PlatformStockInfo();
            stockInfo.setPlatform(platformProduct.getPlatform());
            stockInfo.setPlatformProduct(platformProduct);
            stockInfo.setCurrentStock(BigDecimal.valueOf(platformProduct.getPlatformStockQuantity()));
            stockInfo.setAtpStock(atpResult != null ? atpResult.getAvailableToPromise() : BigDecimal.ZERO);
            stockInfo.setLastSync(platformProduct.getLastSyncAt());
            stockInfo.setSyncStatus("SYNCED");
            
            platformStocks.add(stockInfo);
        }
        
        report.setPlatformStocks(platformStocks);
        
        // Özet oluştur
        StringBuilder summary = new StringBuilder();
        summary.append("ATP Raporu - ").append(material.getMaterialCode()).append("\n");
        summary.append("Mevcut Stok: ").append(material.getCurrentStock()).append("\n");
        if (atpResult != null) {
            summary.append("ATP Miktarı: ").append(atpResult.getAvailableToPromise()).append("\n");
            summary.append("Hesaplama Yöntemi: ").append(atpResult.getCalculationMethod()).append("\n");
            if (atpResult.hasConstraints()) {
                summary.append("Kısıtlamalar: ").append(atpResult.getConstraints().size()).append(" adet\n");
            }
        }
        summary.append("Platform Sayısı: ").append(platformStocks.size()).append("\n");
        
        report.setSummary(summary.toString());
        
        return report;
    }
    
    @Override
    public StockConstraintAnalysis analyzeStockConstraints(Long materialId) {
        MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
        if (material == null) {
            return null;
        }
        
        StockConstraintAnalysis analysis = new StockConstraintAnalysis();
        analysis.setMaterial(material);
        analysis.setAnalysisDate(LocalDateTime.now());
        
        // ATP hesapla
        ATPResult atpResult = calculateATP(materialId);
        if (atpResult == null) {
            return analysis;
        }
        
        analysis.setCurrentATP(atpResult.getAvailableToPromise());
        
        // Kısıtlamaları analiz et
        if (atpResult.hasConstraints()) {
            List<ATPConstraint> constraints = atpResult.getConstraints();
            
            // Birincil kısıtlamalar (en kısıtlayıcı olanlar)
            List<ATPConstraint> primaryConstraints = constraints.stream()
                .filter(ATPConstraint::isConstraint)
                .sorted(Comparator.comparing(ATPConstraint::getConstraintQuantity))
                .limit(3)
                .collect(Collectors.toList());
            
            // İkincil kısıtlamalar (diğerleri)
            List<ATPConstraint> secondaryConstraints = constraints.stream()
                .filter(ATPConstraint::isConstraint)
                .sorted(Comparator.comparing(ATPConstraint::getConstraintQuantity))
                .skip(3)
                .collect(Collectors.toList());
            
            analysis.setPrimaryConstraints(primaryConstraints);
            analysis.setSecondaryConstraints(secondaryConstraints);
            
            // Maksimum mümkün ATP'yi hesapla
            if (!primaryConstraints.isEmpty()) {
                ATPConstraint mainConstraint = primaryConstraints.get(0);
                analysis.setMaxPossibleATP(mainConstraint.getConstraintQuantity());
            }
        }
        
        // Analiz özeti oluştur
        StringBuilder summary = new StringBuilder();
        summary.append("Stok Kısıtlama Analizi\n");
        summary.append("Mevcut ATP: ").append(analysis.getCurrentATP()).append("\n");
        summary.append("Maksimum ATP: ").append(analysis.getMaxPossibleATP()).append("\n");
        summary.append("Birincil Kısıtlamalar: ").append(analysis.getPrimaryConstraints().size()).append("\n");
        summary.append("İkincil Kısıtlamalar: ").append(analysis.getSecondaryConstraints().size()).append("\n");
        
        analysis.setAnalysisSummary(summary.toString());
        
        return analysis;
    }
    
    @Override
    public List<StockConstraintRecommendation> getStockConstraintRecommendations(Long materialId) {
        List<StockConstraintRecommendation> recommendations = new ArrayList<>();
        
        StockConstraintAnalysis analysis = analyzeStockConstraints(materialId);
        if (analysis == null || analysis.getPrimaryConstraints().isEmpty()) {
            return recommendations;
        }
        
        // Her birincil kısıtlama için öneri oluştur
        for (ATPConstraint constraint : analysis.getPrimaryConstraints()) {
            StockConstraintRecommendation recommendation = new StockConstraintRecommendation();
            recommendation.setMaterial(constraint.getConstraintMaterial());
            recommendation.setRecommendationType("STOCK_INCREASE");
            recommendation.setDescription(constraint.getConstraintMaterial().getMaterialName() + 
                " stok miktarını artırın. Mevcut: " + constraint.getAvailableQuantity() + 
                ", Gerekli: " + constraint.getRequiredQuantity());
            recommendation.setSuggestedQuantity(constraint.getRequiredQuantity().multiply(BigDecimal.valueOf(2)));
            recommendation.setExpectedImprovement(constraint.getConstraintQuantity());
            recommendation.setPriority("HIGH");
            recommendation.setRecommendedDate(LocalDateTime.now().plusDays(7));
            
            recommendations.add(recommendation);
        }
        
        return recommendations;
    }
    
    @Override
    public List<ATPCalculationHistory> getATPCalculationHistory(Long materialId) {
        // Bu kısım ATPCalculationHistory repository'sinden gelecek
        // Şimdilik boş liste döndürüyoruz
        return new ArrayList<>();
    }
    
    @Override
    public PlatformStockSyncResult syncPlatformStocks(Long platformId) {
        Platform platform = platformRepository.findById(platformId).orElse(null);
        if (platform == null) {
            return null;
        }
        
        PlatformStockSyncResult result = new PlatformStockSyncResult();
        result.setPlatform(platform);
        result.setSyncDate(LocalDateTime.now());
        
        try {
            List<PlatformProduct> platformProducts = platformProductRepository.findByPlatformId(platformId);
            result.setTotalProducts(platformProducts.size());
            
            int successCount = 0;
            int failedCount = 0;
            
            for (PlatformProduct platformProduct : platformProducts) {
                try {
                    // ATP hesapla
                    ATPResult atpResult = calculateATP(platformProduct.getProduct().getId());
                    if (atpResult != null) {
                        // Platform stok miktarını güncelle
                        platformProduct.setPlatformStockQuantity(atpResult.getAvailableToPromise().intValue());
                        platformProduct.setLastSyncAt(LocalDateTime.now());
                        platformProductRepository.save(platformProduct);
                        successCount++;
                    } else {
                        failedCount++;
                        result.getErrors().add("ATP hesaplanamadı: " + platformProduct.getProduct().getMaterialCode());
                    }
                } catch (Exception e) {
                    failedCount++;
                    result.getErrors().add("Stok güncelleme hatası: " + e.getMessage());
                }
            }
            
            result.setSyncedProducts(successCount);
            result.setFailedProducts(failedCount);
            result.setSyncStatus(successCount == platformProducts.size() ? "SUCCESS" : "PARTIAL");
            
        } catch (Exception e) {
            result.setSyncStatus("FAILED");
            result.getErrors().add("Genel hata: " + e.getMessage());
        }
        
        return result;
    }
    
    private boolean hasBOM(Long materialId) {
        // Bu kısım BOM repository'sinden gelecek
        // Şimdilik false döndürüyoruz
        return false;
    }
}