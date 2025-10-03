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
    private final MaterialStockMovementRepository materialStockMovementRepository;
    private final BomExplosionService bomExplosionService;
    private final StockSyncService stockSyncService;
    
    @Autowired
    public OrderProcessingServiceImpl(OrderRepository orderRepository,
                                   OrderItemRepository orderItemRepository,
                                   MaterialCardRepository materialCardRepository,
                                   MaterialStockMovementRepository materialStockMovementRepository,
                                   BomExplosionService bomExplosionService,
                                   StockSyncService stockSyncService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.materialCardRepository = materialCardRepository;
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
            MaterialCard material = materialCardRepository.findById(item.getProduct().getId()).orElse(null);
            if (material == null || material.getCurrentStock().compareTo(item.getQuantity()) < 0) {
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
                MaterialCard material = materialCardRepository.findById(item.getProduct().getId()).orElse(null);
                if (material != null) {
                    // Stok rezervasyonu için geçici olarak stok miktarını azalt
                    material.setCurrentStock(material.getCurrentStock().subtract(item.getQuantity()));
                    materialCardRepository.save(material);
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
                MaterialCard material = materialCardRepository.findById(item.getProduct().getId()).orElse(null);
                if (material != null) {
                    // Rezerve edilen stokları geri yükle
                    material.setCurrentStock(material.getCurrentStock().add(item.getQuantity()));
                    materialCardRepository.save(material);
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
            MaterialCard material = materialCardRepository.findById(item.getProduct().getId()).orElse(null);
            if (material != null) {
                material.setCurrentStock(material.getCurrentStock().add(item.getQuantity()));
                materialCardRepository.save(material);
                
                createStockMovement(material.getId(), item.getQuantity().doubleValue(), "RETURN", "İade: " + order.getOrderNumber());
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
}