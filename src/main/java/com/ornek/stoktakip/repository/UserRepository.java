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
    
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByRoleAndIsActiveTrue(User.UserRole role);
    
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.lastLoginAt < :date")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.lastLoginAt IS NULL")
    List<User> findUsersNeverLoggedIn();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true AND u.role = :role")
    long countActiveByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.lastLoginAt DESC")
    List<User> findActiveUsersOrderByLastLogin();
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findActiveUsersOrderByCreatedAt();
    
    // Eksik metodlar
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByIsActiveFalse();
}