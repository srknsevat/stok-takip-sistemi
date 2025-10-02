package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Sipariş numarası zorunludur")
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber; // Platform'daki sipariş numarası
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    @NotNull(message = "Platform zorunludur")
    private Platform platform;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;
    
    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "currency", length = 3)
    private String currency = "TRY";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "platform_order_id")
    private String platformOrderId; // Platform'daki orijinal sipariş ID'si
    
    @Column(name = "platform_order_url")
    private String platformOrderUrl; // Platform'daki sipariş linki
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
    
    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;
    
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // İlişkiler
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum OrderStatus {
        PENDING("Beklemede"),
        CONFIRMED("Onaylandı"),
        PROCESSING("İşleniyor"),
        SHIPPED("Kargoya Verildi"),
        DELIVERED("Teslim Edildi"),
        CANCELLED("İptal Edildi"),
        RETURNED("İade Edildi"),
        REFUNDED("Para İadesi Yapıldı");
        
        private final String displayName;
        
        OrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentStatus {
        PENDING("Beklemede"),
        PAID("Ödendi"),
        PARTIALLY_PAID("Kısmen Ödendi"),
        REFUNDED("Para İadesi Yapıldı"),
        FAILED("Ödeme Başarısız");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Order() {}
    
    public Order(String orderNumber, Platform platform) {
        this.orderNumber = orderNumber;
        this.platform = platform;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPlatformOrderId() {
        return platformOrderId;
    }
    
    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }
    
    public String getPlatformOrderUrl() {
        return platformOrderUrl;
    }
    
    public void setPlatformOrderUrl(String platformOrderUrl) {
        this.platformOrderUrl = platformOrderUrl;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDateTime getShippingDate() {
        return shippingDate;
    }
    
    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }
    
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }
    
    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
