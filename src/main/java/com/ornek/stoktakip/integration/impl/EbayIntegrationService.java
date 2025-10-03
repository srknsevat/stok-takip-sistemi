package com.ornek.stoktakip.integration.impl;

import com.ornek.stoktakip.entity.Platform;
import com.ornek.stoktakip.entity.PlatformProduct;
import com.ornek.stoktakip.entity.Product;
import com.ornek.stoktakip.integration.PlatformIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EbayIntegrationService implements PlatformIntegrationService {
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public EbayIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PlatformProduct createProduct(Platform platform, Product product) {
        try {
            // eBay API endpoint'i
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item";
            
            // eBay için ürün verilerini hazırla
            Map<String, Object> productData = prepareProductData(platform, product);
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(productData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.CREATED) {
                Map<String, Object> responseBody = response.getBody();
                String platformProductId = (String) responseBody.get("sku");
                
                PlatformProduct platformProduct = new PlatformProduct(platform, product, platformProductId, product.getCode());
                platformProduct.setPlatformSku(product.getCode());
                platformProduct.setPlatformTitle(product.getName());
                platformProduct.setPlatformPrice(product.getPrice());
                platformProduct.setPlatformStockQuantity(product.getStockQuantity());
                platformProduct.setIsActive(true);
                platformProduct.setIsSynced(true);
                platformProduct.setLastSyncAt(LocalDateTime.now());
                
                return platformProduct;
            }
            
            throw new RuntimeException("eBay'de ürün oluşturulamadı: " + response.getStatusCode());
            
        } catch (Exception e) {
            throw new RuntimeException("eBay entegrasyon hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PlatformProduct updateProduct(Platform platform, Product product, PlatformProduct platformProduct) {
        try {
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item/" + platformProduct.getPlatformProductId();
            
            Map<String, Object> productData = prepareProductData(platform, product);
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(productData, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                platformProduct.setPlatformTitle(product.getName());
                platformProduct.setPlatformPrice(product.getPrice());
                platformProduct.setPlatformStockQuantity(product.getStockQuantity());
                platformProduct.setIsSynced(true);
                platformProduct.setLastSyncAt(LocalDateTime.now());
                
                return platformProduct;
            }
            
            throw new RuntimeException("eBay'de ürün güncellenemedi: " + response.getStatusCode());
            
        } catch (Exception e) {
            throw new RuntimeException("eBay güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteProduct(Platform platform, PlatformProduct platformProduct) {
        try {
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item/" + platformProduct.getPlatformProductId();
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            
            return response.getStatusCode() == HttpStatus.NO_CONTENT;
            
        } catch (Exception e) {
            throw new RuntimeException("eBay silme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateStock(Platform platform, Product product, Integer newStockQuantity) {
        try {
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item/" + product.getCode() + "/availability";
            
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("sku", product.getCode());
            stockData.put("availability", Map.of(
                "shipToLocationAvailability", Map.of(
                    "quantity", newStockQuantity
                )
            ));
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(stockData, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            throw new RuntimeException("eBay stok güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PlatformProduct getProductFromPlatform(Platform platform, String platformProductId) {
        try {
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item/" + platformProductId;
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> productData = response.getBody();
                return processPlatformProductData(platform, productData);
            }
            
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("eBay ürün getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<PlatformProduct> getAllProductsFromPlatform(Platform platform) {
        try {
            String url = platform.getApiEndpoint() + "/sell/inventory/v1/inventory_item";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> products = (List<Map<String, Object>>) responseBody.get("inventoryItems");
                
                List<PlatformProduct> platformProducts = new ArrayList<>();
                for (Map<String, Object> productData : products) {
                    Product product = processPlatformProductData(platform, productData);
                    PlatformProduct platformProduct = new PlatformProduct(platform, product, product.getCode(), product.getCode());
                    if (platformProduct != null) {
                        platformProducts.add(platformProduct);
                    }
                }
                
                return platformProducts;
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            throw new RuntimeException("eBay ürün listesi getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean testConnection(Platform platform) {
        try {
            String url = platform.getApiEndpoint() + "/sell/account/v1/privilege";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getOrdersFromPlatform(Platform platform, String startDate, String endDate) {
        try {
            String url = platform.getApiEndpoint() + "/sell/fulfillment/v1/order?filter=creationdate:[" + startDate + ".." + endDate + "]";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return (List<Map<String, Object>>) responseBody.get("orders");
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            throw new RuntimeException("eBay sipariş getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateOrderStatus(Platform platform, String platformOrderId, String newStatus) {
        try {
            String url = platform.getApiEndpoint() + "/sell/fulfillment/v1/order/" + platformOrderId + "/shipping_fulfillment";
            
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("fulfillmentInstructions", Map.of(
                "shippingStep", Map.of(
                    "shippingCarrierCode", "OTHER",
                    "shippingServiceCode", "OTHER"
                )
            ));
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(statusData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            throw new RuntimeException("eBay sipariş durumu güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean processWebhook(Platform platform, Map<String, Object> webhookData) {
        try {
            // eBay webhook verilerini işle
            String eventType = (String) webhookData.get("eventType");
            
            switch (eventType) {
                case "ORDER_CREATED":
                    return processOrderCreated(platform, webhookData);
                case "ORDER_UPDATED":
                    return processOrderUpdated(platform, webhookData);
                case "INVENTORY_UPDATED":
                    return processInventoryUpdated(platform, webhookData);
                default:
                    return false;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("eBay webhook işleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> prepareProductData(Platform platform, Product product) {
        Map<String, Object> productData = new HashMap<>();
        
        productData.put("sku", product.getCode());
        productData.put("condition", "NEW");
        productData.put("conditionDescription", product.getDescription());
        
        Map<String, Object> availability = new HashMap<>();
        availability.put("shipToLocationAvailability", Map.of(
            "quantity", product.getStockQuantity()
        ));
        productData.put("availability", availability);
        
        Map<String, Object> packageWeightAndSize = new HashMap<>();
        packageWeightAndSize.put("weight", Map.of("value", 1.0, "unit", "POUND"));
        productData.put("packageWeightAndSize", packageWeightAndSize);
        
        return productData;
    }
    
    @Override
    public Product processPlatformProductData(Platform platform, Map<String, Object> platformData) {
        // eBay'den gelen veriyi Product entity'sine dönüştür
        Product product = new Product();
        product.setCode((String) platformData.get("sku"));
        product.setName((String) platformData.get("title"));
        product.setDescription((String) platformData.get("description"));
        
        // Fiyat bilgisini çıkar (eBay'de fiyat genellikle listing'de tutulur)
        Map<String, Object> pricing = (Map<String, Object>) platformData.get("pricing");
        if (pricing != null) {
            Map<String, Object> price = (Map<String, Object>) pricing.get("price");
            if (price != null) {
                product.setPrice(new BigDecimal(price.get("value").toString()));
            }
        }
        
        // Stok bilgisini çıkar
        Map<String, Object> availability = (Map<String, Object>) platformData.get("availability");
        if (availability != null) {
            Map<String, Object> shipToLocation = (Map<String, Object>) availability.get("shipToLocationAvailability");
            if (shipToLocation != null) {
                product.setStockQuantity((Integer) shipToLocation.get("quantity"));
            }
        }
        
        return product;
    }
    
    private HttpHeaders createHeaders(Platform platform) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + platform.getAccessToken());
        headers.set("X-EBAY-C-MARKETPLACE-ID", "EBAY_TR"); // Türkiye marketplace
        return headers;
    }
    
    private boolean processOrderCreated(Platform platform, Map<String, Object> webhookData) {
        // Sipariş oluşturma işlemi
        return true;
    }
    
    private boolean processOrderUpdated(Platform platform, Map<String, Object> webhookData) {
        // Sipariş güncelleme işlemi
        return true;
    }
    
    private boolean processInventoryUpdated(Platform platform, Map<String, Object> webhookData) {
        // Envanter güncelleme işlemi
        return true;
    }
    
    @Override
    public String getPlatformName() {
        return "eBay";
    }
    
    @Override
    public String getPlatformCode() {
        return "EBAY";
    }
    
    @Override
    public String getApiVersion() {
        return "v1.0";
    }
    
    @Override
    public String processErrorMessage(Platform platform, Exception exception) {
        return "eBay API Hatası: " + exception.getMessage();
    }
    
    @Override
    public boolean checkRateLimit(Platform platform) {
        // eBay rate limit kontrolü
        return true;
    }
    
    @Override
    public boolean retryOperation(Platform platform, Runnable operation, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                operation.run();
                return true;
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    System.err.println("eBay işlem başarısız: " + e.getMessage());
                    return false;
                }
                try {
                    Thread.sleep(1000 * (i + 1)); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}
