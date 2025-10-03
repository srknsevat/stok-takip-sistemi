package com.ornek.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Kullanıcı adı zorunludur")
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "E-posta zorunludur")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Şifre zorunludur")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Ad zorunludur")
    @Column(nullable = false)
    private String firstName;
    
    @NotBlank(message = "Soyad zorunludur")
    @Column(nullable = false)
    private String lastName;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // İlişkiler
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs;
    
    // Enums
    public enum UserRole {
        SUPER_ADMIN,    // Süper yönetici
        ADMIN,          // Yönetici
        MANAGER,        // Müdür
        OPERATOR,       // Operatör
        VIEWER          // Görüntüleyici
    }
    
    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String password, String firstName, String lastName, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    public List<AuditLog> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<AuditLog> auditLogs) { this.auditLogs = auditLogs; }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }
    
    public boolean isAdmin() {
        return role == UserRole.SUPER_ADMIN || role == UserRole.ADMIN;
    }
    
    public boolean canManageUsers() {
        return role == UserRole.SUPER_ADMIN || role == UserRole.ADMIN;
    }
    
    // Spring Security için gerekli metodlar
    public boolean isAccountNonExpired() {
        return isActive;
    }
    
    public boolean isAccountNonLocked() {
        return isActive;
    }
    
    public boolean isCredentialsNonExpired() {
        return isActive;
    }
    
    public boolean isEnabled() {
        return isActive;
    }
    
    public java.util.Collection<String> getRoleNames() {
        java.util.List<String> roleNames = new java.util.ArrayList<>();
        if (roles != null) {
            for (Role role : roles) {
                roleNames.add("ROLE_" + role.getName().toUpperCase());
            }
        }
        return roleNames;
    }
    
    public boolean hasAnyRole(String... roleNames) {
        if (roles == null) return false;
        for (String roleName : roleNames) {
            for (Role role : roles) {
                if (role.getName().equalsIgnoreCase(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }
}