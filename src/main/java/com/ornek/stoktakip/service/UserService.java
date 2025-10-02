package com.ornek.stoktakip.service;

import com.ornek.stoktakip.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    
    // Kullanıcı CRUD işlemleri
    List<User> getAllUsers();
    
    List<User> getActiveUsers();
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    User saveUser(User user);
    
    User updateUser(User user);
    
    void deleteUser(Long id);
    
    // Kullanıcı oluşturma
    User createUser(String username, String email, String password, String firstName, String lastName, Set<User.Role> roles);
    
    User createUserByAdmin(String username, String email, String password, String firstName, String lastName, Set<User.Role> roles, Long createdBy);
    
    // Kimlik doğrulama
    boolean authenticateUser(String username, String password);
    
    boolean isUserExists(String username, String email);
    
    boolean isUsernameAvailable(String username);
    
    boolean isEmailAvailable(String email);
    
    // Şifre işlemleri
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    void resetPassword(String email);
    
    boolean validatePasswordResetToken(String token);
    
    void updatePasswordWithToken(String token, String newPassword);
    
    // E-posta doğrulama
    void sendEmailVerification(String email);
    
    boolean verifyEmail(String token);
    
    // Hesap kilitleme
    void lockUser(Long userId);
    
    void unlockUser(Long userId);
    
    void incrementFailedLoginAttempts(String username);
    
    void resetFailedLoginAttempts(String username);
    
    // Rol yönetimi
    void assignRoles(Long userId, Set<User.Role> roles);
    
    void removeRole(Long userId, User.Role role);
    
    boolean hasRole(Long userId, User.Role role);
    
    boolean hasAnyRole(Long userId, User.Role... roles);
    
    // Kullanıcı arama ve filtreleme
    List<User> searchUsers(String searchTerm);
    
    List<User> getUsersByRole(User.Role role);
    
    List<User> getLockedUsers();
    
    List<User> getInactiveUsers();
    
    // İstatistikler
    long getTotalUserCount();
    
    long getActiveUserCount();
    
    long getUserCountByRole(User.Role role);
    
    // Son giriş güncelleme
    void updateLastLogin(String username);
}
