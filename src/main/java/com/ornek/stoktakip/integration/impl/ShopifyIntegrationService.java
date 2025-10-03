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
public class ShopifyIntegrationService implements PlatformIntegrationService {
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public ShopifyIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public PlatformProduct createProduct(Platform platform, Product product) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/products.json";
            
            Map<String, Object> productData = prepareProductData(platform, product);
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(productData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.CREATED) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> productResponse = (Map<String, Object>) responseBody.get("product");
                Long platformProductId = ((Number) productResponse.get("id")).longValue();
                
                PlatformProduct platformProduct = new PlatformProduct(platform, product, platformProductId.toString(), product.getCode());
                platformProduct.setPlatformSku(product.getCode());
                platformProduct.setPlatformTitle(product.getName());
                platformProduct.setPlatformPrice(product.getPrice());
                platformProduct.setPlatformStockQuantity(product.getStockQuantity());
                platformProduct.setIsActive(true);
                platformProduct.setIsSynced(true);
                platformProduct.setLastSyncAt(LocalDateTime.now());
                
                return platformProduct;
            }
            
            throw new RuntimeException("Shopify'de ürün oluşturulamadı: " + response.getStatusCode());
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify entegrasyon hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PlatformProduct updateProduct(Platform platform, Product product, PlatformProduct platformProduct) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/products/" + platformProduct.getPlatformProductId() + ".json";
            
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
            
            throw new RuntimeException("Shopify'de ürün güncellenemedi: " + response.getStatusCode());
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteProduct(Platform platform, PlatformProduct platformProduct) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/products/" + platformProduct.getPlatformProductId() + ".json";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify silme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateStock(Platform platform, Product product, Integer newStockQuantity) {
        try {
            // Shopify'de stok güncelleme için önce variant'ı bul
            String variantUrl = platform.getApiEndpoint() + "/admin/api/2023-10/products.json?fields=id,variants&limit=250";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(variantUrl, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> products = (List<Map<String, Object>>) responseBody.get("products");
                
                for (Map<String, Object> shopifyProduct : products) {
                    List<Map<String, Object>> variants = (List<Map<String, Object>>) shopifyProduct.get("variants");
                    for (Map<String, Object> variant : variants) {
                        String sku = (String) variant.get("sku");
                        if (product.getCode().equals(sku)) {
                            Long variantId = ((Number) variant.get("id")).longValue();
                            
                            // Stok güncelleme
                            String inventoryUrl = platform.getApiEndpoint() + "/admin/api/2023-10/inventory_levels/set.json";
                            
                            Map<String, Object> inventoryData = new HashMap<>();
                            inventoryData.put("location_id", 1L); // Ana lokasyon ID'si
                            inventoryData.put("inventory_item_id", variantId);
                            inventoryData.put("available", newStockQuantity);
                            
                            HttpEntity<Map<String, Object>> inventoryRequest = new HttpEntity<>(inventoryData, headers);
                            ResponseEntity<Map> inventoryResponse = restTemplate.postForEntity(inventoryUrl, inventoryRequest, Map.class);
                            
                            return inventoryResponse.getStatusCode() == HttpStatus.OK;
                        }
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify stok güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PlatformProduct getProductFromPlatform(Platform platform, String platformProductId) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/products/" + platformProductId + ".json";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> productData = (Map<String, Object>) responseBody.get("product");
                return processPlatformProductData(platform, productData);
            }
            
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify ürün getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<PlatformProduct> getAllProductsFromPlatform(Platform platform) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/products.json?limit=250";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> products = (List<Map<String, Object>>) responseBody.get("products");
                
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
            throw new RuntimeException("Shopify ürün listesi getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean testConnection(Platform platform) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/shop.json";
            
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
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/orders.json?created_at_min=" + startDate + "&created_at_max=" + endDate + "&limit=250";
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return (List<Map<String, Object>>) responseBody.get("orders");
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify sipariş getirme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateOrderStatus(Platform platform, String platformOrderId, String newStatus) {
        try {
            String url = platform.getApiEndpoint() + "/admin/api/2023-10/orders/" + platformOrderId + "/fulfillments.json";
            
            Map<String, Object> fulfillmentData = new HashMap<>();
            fulfillmentData.put("fulfillment", Map.of(
                "status", newStatus,
                "tracking_number", "",
                "tracking_company", "OTHER"
            ));
            
            HttpHeaders headers = createHeaders(platform);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(fulfillmentData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            return response.getStatusCode() == HttpStatus.CREATED;
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify sipariş durumu güncelleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean processWebhook(Platform platform, Map<String, Object> webhookData) {
        try {
            String topic = (String) webhookData.get("topic");
            
            switch (topic) {
                case "orders/create":
                    return processOrderCreated(platform, webhookData);
                case "orders/updated":
                    return processOrderUpdated(platform, webhookData);
                case "orders/paid":
                    return processOrderPaid(platform, webhookData);
                case "orders/cancelled":
                    return processOrderCancelled(platform, webhookData);
                case "inventory_levels/update":
                    return processInventoryUpdated(platform, webhookData);
                default:
                    return false;
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Shopify webhook işleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> prepareProductData(Platform platform, Product product) {
        Map<String, Object> productData = new HashMap<>();
        
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("title", product.getName());
        productInfo.put("body_html", product.getDescription());
        productInfo.put("vendor", "Stok Takip Sistemi");
        productInfo.put("product_type", "General");
        productInfo.put("status", "active");
        
        // Variant bilgileri
        Map<String, Object> variant = new HashMap<>();
        variant.put("sku", product.getCode());
        variant.put("price", product.getPrice().toString());
        variant.put("inventory_quantity", product.getStockQuantity());
        variant.put("inventory_management", "shopify");
        variant.put("inventory_policy", "deny");
        variant.put("fulfillment_service", "manual");
        
        List<Map<String, Object>> variants = Arrays.asList(variant);
        productInfo.put("variants", variants);
        
        productData.put("product", productInfo);
        
        return productData;
    }
    
    @Override
    public Product processPlatformProductData(Platform platform, Map<String, Object> platformData) {
        Product product = new Product();
        product.setName((String) platformData.get("title"));
        product.setDescription((String) platformData.get("body_html"));
        
        // Variant bilgilerini al
        List<Map<String, Object>> variants = (List<Map<String, Object>>) platformData.get("variants");
        if (variants != null && !variants.isEmpty()) {
            Map<String, Object> firstVariant = variants.get(0);
            product.setCode((String) firstVariant.get("sku"));
            product.setPrice(new BigDecimal(firstVariant.get("price").toString()));
            product.setStockQuantity((Integer) firstVariant.get("inventory_quantity"));
        }
        
        return product;
    }
    
    private HttpHeaders createHeaders(Platform platform) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", platform.getAccessToken());
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
    
    private boolean processOrderPaid(Platform platform, Map<String, Object> webhookData) {
        // Sipariş ödeme işlemi
        return true;
    }
    
    private boolean processOrderCancelled(Platform platform, Map<String, Object> webhookData) {
        // Sipariş iptal işlemi
        return true;
    }
    
    private boolean processInventoryUpdated(Platform platform, Map<String, Object> webhookData) {
        // Envanter güncelleme işlemi
        return true;
    }
    
    @Override
    public boolean retryOperation(Platform platform, Runnable operation, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                operation.run();
                return true; // Başarılı olursa çık
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxRetries) {
                    return false; // Maksimum deneme sayısına ulaşıldı
                }
                // Kısa bir bekleme sonrası tekrar dene
                try {
                    Thread.sleep(1000 * attempts); // Artan bekleme süresi
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false; // İşlem kesintiye uğradı
                }
            }
        }
        return false; // Hiçbir deneme başarılı olmadı
    }
}
