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
import java.util.Set;
import java.util.UUID;

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
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
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
            // Yeni kullanıcı - şifreyi şifrele
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + user.getId()));
        
        // Şifre değişmemişse mevcut şifreyi koru
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public User createUser(String username, String email, String password, String firstName, String lastName, Set<User.Role> roles) {
        if (isUserExists(username, email)) {
            throw new RuntimeException("Kullanıcı adı veya e-posta zaten kullanımda");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(roles);
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        
        return userRepository.save(user);
    }
    
    @Override
    public User createUserByAdmin(String username, String email, String password, String firstName, String lastName, Set<User.Role> roles, Long createdBy) {
        if (isUserExists(username, email)) {
            throw new RuntimeException("Kullanıcı adı veya e-posta zaten kullanımda");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(roles);
        user.setIsActive(true);
        user.setEmailVerified(true); // Admin tarafından oluşturulan kullanıcılar otomatik doğrulanır
        user.setCreatedBy(createdBy);
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username, username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Hesap kilitli mi kontrol et
        if (!user.isAccountNonLocked()) {
            return false;
        }
        
        // Şifre kontrolü
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        
        if (passwordMatches) {
            resetFailedLoginAttempts(username);
            updateLastLogin(username);
        } else {
            incrementFailedLoginAttempts(username);
        }
        
        return passwordMatches;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.findByUsername(username).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifre yanlış");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-posta adresi bulunamadı: " + email));
        
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(24)); // 24 saat geçerli
        
        userRepository.save(user);
        
        // E-posta gönderme işlemi burada yapılabilir
        System.out.println("Şifre sıfırlama token'ı: " + token);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validatePasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token, LocalDateTime.now()).isPresent();
    }
    
    @Override
    public void updatePasswordWithToken(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş token"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        
        userRepository.save(user);
    }
    
    @Override
    public void sendEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-posta adresi bulunamadı: " + email));
        
        if (user.getEmailVerified()) {
            throw new RuntimeException("E-posta zaten doğrulanmış");
        }
        
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        userRepository.save(user);
        
        // E-posta gönderme işlemi burada yapılabilir
        System.out.println("E-posta doğrulama token'ı: " + token);
    }
    
    @Override
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    @Override
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setLockedUntil(LocalDateTime.now().plusHours(24)); // 24 saat kilitle
        userRepository.save(user);
    }
    
    @Override
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
    
    @Override
    public void incrementFailedLoginAttempts(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
        }
    }
    
    @Override
    public void resetFailedLoginAttempts(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.resetFailedLoginAttempts();
            userRepository.save(user);
        }
    }
    
    @Override
    public void assignRoles(Long userId, Set<User.Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        user.setRoles(roles);
        userRepository.save(user);
    }
    
    @Override
    public void removeRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        if (user.getRoles() != null) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        return user.hasRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyRole(Long userId, User.Role... roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
        
        return user.hasAnyRole(roles);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findBySearchTerm(searchTerm);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRolesContaining(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getLockedUsers() {
        return userRepository.findLockedUsers(LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getInactiveUsers() {
        return userRepository.findInactiveUsers(LocalDateTime.now().minusDays(30)); // 30 gün önce
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
    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
    
    @Override
    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
