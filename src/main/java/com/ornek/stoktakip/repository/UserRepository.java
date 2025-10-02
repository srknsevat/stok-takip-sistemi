package com.ornek.stoktakip.repository;

import com.ornek.stoktakip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByEmailVerifiedTrue();
    
    List<User> findByRolesContaining(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.emailVerified = true")
    List<User> findActiveAndVerifiedUsers();
    
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= 5 AND u.lockedUntil > :now")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :beforeDate")
    List<User> findInactiveUsers(@Param("beforeDate") LocalDateTime beforeDate);
    
    @Query("SELECT u FROM User u WHERE u.passwordResetToken = :token AND u.passwordResetExpires > :now")
    Optional<User> findByPasswordResetToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token")
    Optional<User> findByEmailVerificationToken(@Param("token") String token);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.roles = :role")
    long countByRole(@Param("role") User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.createdBy = :createdBy")
    List<User> findByCreatedBy(@Param("createdBy") Long createdBy);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:search% OR u.email LIKE %:search% OR u.firstName LIKE %:search% OR u.lastName LIKE %:search%")
    List<User> findBySearchTerm(@Param("search") String search);
}
