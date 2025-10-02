package com.ornek.stoktakip.service.impl;

import com.ornek.stoktakip.entity.User;
import com.ornek.stoktakip.repository.UserRepository;
import com.ornek.stoktakip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + user.getId()));
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public User createUser(String username, String email, String password, String firstName, String lastName, User.UserRole role) {
        if (isUsernameExists(username)) {
            throw new RuntimeException("Kullanıcı adı zaten kullanımda: " + username);
        }
        
        if (isEmailExists(email)) {
            throw new RuntimeException("E-posta adresi zaten kullanımda: " + email);
        }
        
        User user = new User(username, email, passwordEncoder.encode(password), firstName, lastName, role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getInactiveUsers() {
        return userRepository.findByIsActiveFalse();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRoleAndActive(User.UserRole role, boolean isActive) {
        if (isActive) {
            return userRepository.findByRoleAndIsActiveTrue(role);
        } else {
            return userRepository.findByRole(role).stream()
                    .filter(user -> !user.getIsActive())
                    .toList();
        }
    }
    
    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void resetPassword(Long userId, String newPassword) {
        changePassword(userId, newPassword);
    }
    
    @Override
    public boolean validatePassword(String password) {
        // Şifre validasyon kuralları
        return password != null && password.length() >= 6;
    }
    
    @Override
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public void updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username, Long excludeId) {
        Optional<User> existing = userRepository.findByUsername(username);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email, Long excludeId) {
        Optional<User> existing = userRepository.findByEmail(email);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getInactiveUserCount() {
        return getTotalUserCount() - getActiveUserCount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canUserManageUsers(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }
        
        // Süper admin herkesi yönetebilir
        if (currentUser.getRole() == User.UserRole.SUPER_ADMIN) {
            return true;
        }
        
        // Admin sadece kendisinden düşük seviyeli kullanıcıları yönetebilir
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            return targetUser.getRole() != User.UserRole.SUPER_ADMIN;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(User user, User.UserRole role) {
        return user != null && user.getRole() == role;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(User user) {
        return user != null && (user.getRole() == User.UserRole.SUPER_ADMIN || user.getRole() == User.UserRole.ADMIN);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getRecentlyLoggedInUsers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userRepository.findInactiveUsers(cutoffDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersNeverLoggedIn() {
        return userRepository.findUsersNeverLoggedIn();
    }
    
    // Spring Security için gerekli metodlar
    @Override
    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    @Override
    public void incrementFailedLoginAttempts(String username) {
        // Bu kısım için User entity'ye failedLoginAttempts field'ı eklenebilir
        // Şimdilik basit bir implementasyon
        System.out.println("Failed login attempt for user: " + username);
    }
    
    @Override
    public void resetFailedLoginAttempts(String username) {
        // Bu kısım için User entity'ye failedLoginAttempts field'ı eklenebilir
        // Şimdilik basit bir implementasyon
        System.out.println("Reset failed login attempts for user: " + username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String username) {
        // Bu kısım için User entity'ye accountLocked field'ı eklenebilir
        // Şimdilik basit bir implementasyon
        return false;
    }
}