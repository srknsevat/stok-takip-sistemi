package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.dto.OrderProcessingResult;
import com.ornek.stoktakip.entity.*;
import com.ornek.stoktakip.repository.*;
import com.ornek.stoktakip.service.OrderProcessingService;
import com.ornek.stoktakip.service.BomExplosionService;
import com.ornek.stoktakip.service.StockSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class OrderProcessingServiceImpl implements OrderProcessingService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MaterialCardRepository materialCardRepository;
    private final ProductRepository productRepository;
    private final MaterialStockMovementRepository materialStockMovementRepository;
    private final BomExplosionService bomExplosionService;
    private final StockSyncService stockSyncService;
    
    @Autowired
    public OrderProcessingServiceImpl(OrderRepository orderRepository,
                                   OrderItemRepository orderItemRepository,
                                   MaterialCardRepository materialCardRepository,
                                   ProductRepository productRepository,
                                   MaterialStockMovementRepository materialStockMovementRepository,
                                   BomExplosionService bomExplosionService,
                                   StockSyncService stockSyncService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.materialCardRepository = materialCardRepository;
        this.productRepository = productRepository;
        this.materialStockMovementRepository = materialStockMovementRepository;
        this.bomExplosionService = bomExplosionService;
        this.stockSyncService = stockSyncService;
    }
    
    @Override
    public OrderProcessingResult processOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            // Stok kontrolü
            if (!checkStockAvailability(order)) {
                result.setSuccess(false);
                result.setMessage("Yetersiz stok");
                return result;
            }
            
            // Stok rezervasyonu
            if (!reserveStock(order)) {
                result.setSuccess(false);
                result.setMessage("Stok rezervasyonu başarısız");
                return result;
            }
            
            // Sipariş durumunu güncelle
            order.setStatus(Order.OrderStatus.CONFIRMED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            // Stok hareketlerini oluştur
            processOrderItems(order);
            
            // Platform stoklarını güncelle
            updatePlatformStocks(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş başarıyla işlendi");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş işleme hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult cancelOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            // Rezerve edilen stokları serbest bırak
            releaseReservedStock(order);
            
            // Sipariş durumunu güncelle
            order.setStatus(Order.OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş iptal edildi");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş iptal hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult confirmOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            order.setStatus(Order.OrderStatus.CONFIRMED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş onaylandı");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş onaylama hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult shipOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            order.setStatus(Order.OrderStatus.SHIPPED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş sevk edildi");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş sevk hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult deliverOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş teslim edildi");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş teslim hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult returnOrder(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        result.setProcessedAt(LocalDateTime.now());
        
        try {
            // Stokları geri yükle
            restoreStockFromOrder(order);
            
            order.setStatus(Order.OrderStatus.RETURNED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            
            result.setSuccess(true);
            result.setMessage("Sipariş iade edildi");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Sipariş iade hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean checkStockAvailability(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            if (product == null || product.getCurrentStock().compareTo(item.getQuantity()) < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public boolean reserveStock(Order order) {
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                if (product != null) {
                    // Stok rezervasyonu için geçici olarak stok miktarını azalt
                    product.setCurrentStock(product.getCurrentStock().subtract(item.getQuantity()));
                    productRepository.save(product);
                }
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Stok rezervasyonu hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean releaseReservedStock(Order order) {
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                if (product != null) {
                    // Rezerve edilen stokları geri yükle
                    product.setCurrentStock(product.getCurrentStock().add(item.getQuantity()));
                    productRepository.save(product);
                }
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Stok serbest bırakma hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Order updateOrderStatus(Order order, Order.OrderStatus newStatus) {
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    @Override
    public List<OrderItem> processOrderItems(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        for (OrderItem item : orderItems) {
            // BOM patlatma
            Map<MaterialCard, Double> explodedComponents = bomExplosionService.explodeBom(item.getProduct().getId(), item.getQuantity().doubleValue());
            
            // Stok hareketleri oluştur
            for (Map.Entry<MaterialCard, Double> entry : explodedComponents.entrySet()) {
                createStockMovement(entry.getKey().getId(), entry.getValue(), "SALE", "Sipariş: " + order.getOrderNumber());
            }
        }
        
        return orderItems;
    }
    
    @Override
    public Map<Long, Double> explodeBOMForOrder(Order order) {
        Map<Long, Double> explodedComponents = new HashMap<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        for (OrderItem item : orderItems) {
            Map<MaterialCard, Double> components = bomExplosionService.explodeBom(item.getProduct().getId(), item.getQuantity().doubleValue());
            
            for (Map.Entry<MaterialCard, Double> entry : components.entrySet()) {
                Long materialId = entry.getKey().getId();
                Double quantity = entry.getValue();
                
                explodedComponents.merge(materialId, quantity, Double::sum);
            }
        }
        
        return explodedComponents;
    }
    
    @Override
    public void createStockMovement(Long materialId, Double quantity, String movementType, String description) {
        MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
        if (material != null) {
            MaterialStockMovement movement = new MaterialStockMovement();
            movement.setMaterialCard(material);
            movement.setMovementType(movementType);
            movement.setQuantity(quantity);
            movement.setMovementDate(LocalDateTime.now());
            movement.setDescription(description);
            
            materialStockMovementRepository.save(movement);
        }
    }
    
    @Override
    public boolean updatePlatformStocks(Order order) {
        try {
            return stockSyncService.syncAllPlatforms();
        } catch (Exception e) {
            System.err.println("Platform stok güncelleme hatası: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<OrderProcessingResult> getOrderProcessingHistory(Long orderId) {
        // Bu kısım OrderProcessingResult repository'sinden gelecek
        return new ArrayList<>();
    }
    
    @Override
    public List<OrderProcessingResult> processPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING);
        List<OrderProcessingResult> results = new ArrayList<>();
        
        for (Order order : pendingOrders) {
            results.add(processOrder(order));
        }
        
        return results;
    }
    
    @Override
    public Map<String, Object> getOrderProcessingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(Order.OrderStatus.CONFIRMED);
        long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
        
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("confirmedOrders", confirmedOrders);
        stats.put("shippedOrders", shippedOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("cancelledOrders", cancelledOrders);
        
        return stats;
    }
    
    private void restoreStockFromOrder(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            if (product != null) {
                product.setCurrentStock(product.getCurrentStock().add(item.getQuantity()));
                productRepository.save(product);
                
                createStockMovement(product.getId(), item.getQuantity().doubleValue(), "RETURN", "İade: " + order.getOrderNumber());
            }
        }
    }
    
    @Override
    public OrderProcessingService.OrderProductionPlan createOrderProductionPlan(Order order) {
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            List<OrderProcessingService.MrpItem> mrpItems = new ArrayList<>();
            
            for (OrderItem orderItem : orderItems) {
                // BOM patlatma işlemi
                Map<Long, Double> explodedBOM = explodeBOMForOrder(order);
                
                for (Map.Entry<Long, Double> entry : explodedBOM.entrySet()) {
                    Long materialId = entry.getKey();
                    Double requiredQuantity = entry.getValue();
                    
                    // Mevcut stok kontrolü
                    MaterialCard material = materialCardRepository.findById(materialId).orElse(null);
                    Double availableQuantity = material != null ? material.getCurrentStock().doubleValue() : 0.0;
                    
                    OrderProcessingService.MrpItem mrpItem = new OrderProcessingService.MrpItem(
                        materialId,
                        material != null ? material.getMaterialName() : "Bilinmeyen Malzeme",
                        requiredQuantity,
                        availableQuantity
                    );
                    
                    // Durum belirleme
                    if (availableQuantity >= requiredQuantity) {
                        mrpItem.setStatus("YETERLI");
                    } else {
                        mrpItem.setStatus("YETERSIZ");
                    }
                    
                    mrpItem.setUnit(material != null ? material.getUnit() : "ADET");
                    mrpItems.add(mrpItem);
                }
            }
            
            // Plan durumunu belirle
            String planStatus = mrpItems.stream()
                .anyMatch(item -> "YETERSIZ".equals(item.getStatus())) ? "YETERSIZ_STOK" : "HAZIR";
            
            OrderProcessingService.OrderProductionPlan productionPlan = 
                new OrderProcessingService.OrderProductionPlan(order, mrpItems, planStatus);
            
            productionPlan.setNotes("Sipariş " + order.getOrderNumber() + " için üretim planı oluşturuldu");
            
            return productionPlan;
            
        } catch (Exception e) {
            log.error("Üretim planı oluşturulurken hata: " + e.getMessage(), e);
            
            // Hata durumunda boş plan döndür
            OrderProcessingService.OrderProductionPlan errorPlan = 
                new OrderProcessingService.OrderProductionPlan(order, new ArrayList<>(), "HATA");
            errorPlan.setNotes("Üretim planı oluşturulurken hata: " + e.getMessage());
            
            return errorPlan;
        }
    }
    
    @Override
    public OrderStockCheckResult checkOrderStock(Order order) {
        OrderStockCheckResult result = new OrderStockCheckResult(order, true);
        
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            boolean allAvailable = true;
            
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                Integer requiredQuantity = item.getQuantity().intValue();
                Integer availableQuantity = product != null ? product.getCurrentStock().intValue() : 0;
                
                OrderStockCheckResult.StockCheckItem checkItem = 
                    new OrderStockCheckResult.StockCheckItem(
                        product != null ? product.getId() : null,
                        product != null ? product.getProductName() : "Bilinmeyen Ürün",
                        requiredQuantity,
                        availableQuantity
                    );
                
                result.getStockCheckItems().add(checkItem);
                
                if (availableQuantity < requiredQuantity) {
                    allAvailable = false;
                }
            }
            
            result.setStockAvailable(allAvailable);
            result.setMessage(allAvailable ? "Tüm ürünler stokta mevcut" : "Bazı ürünler stokta yetersiz");
            
        } catch (Exception e) {
            log.error("Stok kontrolü hatası: " + e.getMessage(), e);
            result.setStockAvailable(false);
            result.setMessage("Stok kontrolü sırasında hata: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public OrderCostResult calculateOrderCost(Order order) {
        OrderCostResult result = new OrderCostResult(order);
        
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal materialCost = BigDecimal.ZERO;
            
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                if (product != null) {
                    BigDecimal unitCost = product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
                    BigDecimal itemCost = unitCost.multiply(item.getQuantity());
                    
                    totalCost = totalCost.add(itemCost);
                    materialCost = materialCost.add(itemCost);
                    
                    OrderCostResult.CostItem costItem = new OrderCostResult.CostItem(
                        product.getId(),
                        product.getProductName(),
                        item.getQuantity().intValue(),
                        unitCost,
                        "MATERIAL"
                    );
                    
                    result.getCostItems().add(costItem);
                }
            }
            
            // Labor ve overhead maliyetleri (şimdilik sabit)
            BigDecimal laborCost = totalCost.multiply(BigDecimal.valueOf(0.1)); // %10
            BigDecimal overheadCost = totalCost.multiply(BigDecimal.valueOf(0.05)); // %5
            
            result.setMaterialCost(materialCost);
            result.setLaborCost(laborCost);
            result.setOverheadCost(overheadCost);
            result.setTotalCost(totalCost.add(laborCost).add(overheadCost));
            
            // Kar hesaplama
            BigDecimal totalRevenue = order.getTotalAmount();
            result.setProfit(totalRevenue.subtract(result.getTotalCost()));
            
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                result.setProfitMargin(result.getProfit().divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            }
            
        } catch (Exception e) {
            log.error("Maliyet hesaplama hatası: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    @Override
    public OrderProcessingResult processOrderWithBOM(Order order) {
        OrderProcessingResult result = new OrderProcessingResult();
        result.setOrder(order);
        
        try {
            // Önce stok kontrolü yap
            OrderStockCheckResult stockCheck = checkOrderStock(order);
            if (!stockCheck.isStockAvailable()) {
                result.setSuccess(false);
                result.setMessage("Stok yetersiz: " + stockCheck.getMessage());
                return result;
            }
            
            // BOM patlatma işlemi
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            Map<Long, Double> explodedBOM = new HashMap<>();
            
            for (OrderItem item : orderItems) {
                Map<Long, Double> itemBOM = bomExplosionService.explodeBom(item.getProduct().getId(), item.getQuantity().doubleValue());
                for (Map.Entry<Long, Double> entry : itemBOM.entrySet()) {
                    explodedBOM.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
            
            // Stokları güncelle
            for (Map.Entry<Long, Double> entry : explodedBOM.entrySet()) {
                MaterialCard material = materialCardRepository.findById(entry.getKey()).orElse(null);
                if (material != null) {
                    BigDecimal requiredQuantity = BigDecimal.valueOf(entry.getValue());
                    if (material.getCurrentStock().compareTo(requiredQuantity) >= 0) {
                        material.setCurrentStock(material.getCurrentStock().subtract(requiredQuantity));
                        materialCardRepository.save(material);
                        
                        createStockMovement(material.getId(), requiredQuantity.doubleValue(), "EXIT", "BOM Satış: " + order.getOrderNumber());
                    }
                }
            }
            
            // Siparişi işle
            result = processOrder(order);
            
        } catch (Exception e) {
            log.error("BOM ile sipariş işleme hatası: " + e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("BOM ile sipariş işleme hatası: " + e.getMessage());
        }
        
        return result;
    }
}