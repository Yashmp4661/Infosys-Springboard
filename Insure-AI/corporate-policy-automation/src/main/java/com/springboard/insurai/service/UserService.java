package com.springboard.insurai.service;


import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import com.springboard.insurai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Create a new user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        // Save and return the user
        return userRepository.save(user);
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Get users by role
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
 // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Update user
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // Delete user (soft delete - set inactive)
    public void deactivateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
    
    // Get total user count
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    public Optional<User> getUserByActivationToken(String token) {
        return userRepository.findByActivationToken(token);
    }
    
    public Optional<User> findPreApprovedEmployeeByEmail(String email) {
        return userRepository.findPreApprovedEmployeeByEmail(email);
    }
    

    public List<User> getEmployeesByCorporateAdmin(Long corporateAdminId) {
        return userRepository.findByCorporateAdminId(corporateAdminId);
    }

    
}
