package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Sipariş zorunludur")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Ürün zorunludur")
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_product_id")
    private PlatformProduct platformProduct;
    
    @NotNull(message = "Miktar zorunludur")
    @Positive(message = "Miktar pozitif olmalıdır")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "platform_item_id")
    private String platformItemId; // Platform'daki item ID'si
    
    @Column(name = "platform_sku")
    private String platformSku; // Platform'daki SKU
    
    @Column(name = "platform_title")
    private String platformTitle; // Platform'daki ürün başlığı
    
    @Column(name = "is_processed")
    private Boolean isProcessed = false; // Stok işlemi yapıldı mı?
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    // Business Methods
    private void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discountAmount != null) {
                totalPrice = totalPrice.subtract(discountAmount);
            }
            if (taxAmount != null) {
                totalPrice = totalPrice.add(taxAmount);
            }
        }
    }
    
    public void markAsProcessed() {
        this.isProcessed = true;
        this.processedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public PlatformProduct getPlatformProduct() {
        return platformProduct;
    }
    
    public void setPlatformProduct(PlatformProduct platformProduct) {
        this.platformProduct = platformProduct;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public String getPlatformItemId() {
        return platformItemId;
    }
    
    public void setPlatformItemId(String platformItemId) {
        this.platformItemId = platformItemId;
    }
    
    public String getPlatformSku() {
        return platformSku;
    }
    
    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku;
    }
    
    public String getPlatformTitle() {
        return platformTitle;
    }
    
    public void setPlatformTitle(String platformTitle) {
        this.platformTitle = platformTitle;
    }
    
    public Boolean getIsProcessed() {
        return isProcessed;
    }
    
    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
