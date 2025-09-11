package com.springboard.insurai.controller;



import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import com.springboard.insurai.service.UserService;
import com.springboard.insurai.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/test")

public class TestController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder; 
    
    @Autowired
    private EmailService emailService;

    
    // Test endpoint to create a sample user
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin() {
        try {
            User admin = new User(
                "admin", 
                passwordEncoder.encode("password123"), 
                "admin@insurance.com", 
                "System Administrator", 
                UserRole.PROVIDER_ADMIN
            );
            
            User savedUser = userService.createUser(admin);
            return ResponseEntity.ok("Admin user created successfully with ID: " + savedUser.getId());
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // Test endpoint to get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // Test endpoint to get user count
    @GetMapping("/user-count")
    public ResponseEntity<String> getUserCount() {
        long count = userService.getTotalUserCount();
        return ResponseEntity.ok("Total users: " + count);
    }
    
    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Application is running!");
    }
    


    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            boolean emailWorking = emailService.testEmailConnection();
            if (emailWorking) {
                return ResponseEntity.ok("✅ Email service is working! Check your inbox.");
            } else {
                return ResponseEntity.badRequest().body("❌ Email service is not working. Check configuration.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Email test failed: " + e.getMessage());
        }
    }


    
}
