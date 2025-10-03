package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.entity.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    // Temel CRUD operasyonları
    List<User> getAllUsers();
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    User saveUser(User user);
    
    User updateUser(User user);
    
    void deleteUser(Long id);
    
    // Kullanıcı oluşturma
    User createUser(String username, String email, String password, String firstName, String lastName, User.UserRole role);
    
    // Arama ve filtreleme
    List<User> searchUsers(String searchTerm);
    
    List<User> getActiveUsers();
    
    List<User> getInactiveUsers();
    
    List<User> getUsersByRole(User.UserRole role);
    
    List<User> getUsersByRoleAndActive(User.UserRole role, boolean isActive);
    
    // Şifre yönetimi
    void changePassword(Long userId, String newPassword);
    
    void resetPassword(Long userId, String newPassword);
    
    boolean validatePassword(String password);
    
    // Durum yönetimi
    void activateUser(Long userId);
    
    void deactivateUser(Long userId);
    
    void updateLastLogin(Long userId);
    
    // Doğrulama
    boolean isUsernameExists(String username);
    
    boolean isUsernameExists(String username, Long excludeId);
    
    boolean isEmailExists(String email);
    
    boolean isEmailExists(String email, Long excludeId);
    
    // İstatistikler
    long getTotalUserCount();
    
    long getActiveUserCount();
    
    long getInactiveUserCount();
    
    long getUserCountByRole(User.UserRole role);
    
    // Yetki kontrolü
    boolean canUserManageUsers(User currentUser, User targetUser);
    
    boolean hasRole(User user, User.UserRole role);
    
    boolean isAdmin(User user);
    
    // Son giriş yapan kullanıcılar
    List<User> getRecentlyLoggedInUsers(int days);
    
    List<User> getUsersNeverLoggedIn();
    
    // Spring Security için gerekli metodlar
    void updateLastLogin(String username);
    
    void incrementFailedLoginAttempts(String username);
    
    void resetFailedLoginAttempts(String username);
    
    boolean isAccountLocked(String username);
    
    // Admin tarafından kullanıcı oluşturma
    User createUserByAdmin(String username, String email, String password, String firstName, String lastName, List<Role> roles, Long createdBy);
    
    // Eksik metodlar
    boolean isUserExists(String username, String email);
    void lockUser(Long userId);
    void unlockUser(Long userId);
    void assignRoles(Long userId, java.util.Set<Role> roles);
    List<User> getLockedUsers();
}