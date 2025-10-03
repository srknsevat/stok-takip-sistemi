package com.ornek.stoktakip.dto;

import com.ornek.stoktakip.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ATPResult {
    private Product product;
    private BigDecimal availableToPromise;
    private BigDecimal reservedQuantity;
    private BigDecimal totalStock;
    private LocalDateTime calculationDate;
    private String status;
    private List<String> warnings;
    private List<String> errors;

    public ATPResult() {}

    public ATPResult(Product product, BigDecimal availableToPromise, BigDecimal reservedQuantity, BigDecimal totalStock) {
        this.product = product;
        this.availableToPromise = availableToPromise;
        this.reservedQuantity = reservedQuantity;
        this.totalStock = totalStock;
        this.calculationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public BigDecimal getAvailableToPromise() { return availableToPromise; }
    public void setAvailableToPromise(BigDecimal availableToPromise) { this.availableToPromise = availableToPromise; }
    
    public BigDecimal getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(BigDecimal reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    
    public BigDecimal getTotalStock() { return totalStock; }
    public void setTotalStock(BigDecimal totalStock) { this.totalStock = totalStock; }
    
    public LocalDateTime getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDateTime calculationDate) { this.calculationDate = calculationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}