package com.springboard.insurai.repository;



import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    Boolean existsByUsername(String username);
    
    // Check if email exists
    Boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(UserRole role);
    
    // Find active users by role
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    
    Optional<User> findByActivationToken(String activationToken);
    List<User> findByRoleAndPreApprovedTrueAndIsEnabledFalse(UserRole role);
    List<User> findByCorporateAdminId(Long corporateAdminId);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.preApproved = true AND u.role = 'EMPLOYEE'")
    Optional<User> findPreApprovedEmployeeByEmail(@Param("email") String email);
    
    // Count methods
    long countByRole(UserRole role);
    long count();
}

