package com.springboard.insurai.service;



import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import com.springboard.insurai.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializer {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void initializeDefaultData() {
        createDefaultAdminIfNotExists();
    }
    
    private void createDefaultAdminIfNotExists() {
        if (userRepository.findByRole(UserRole.PROVIDER_ADMIN).isEmpty()) {
            User defaultAdmin = new User();
            defaultAdmin.setUsername("superadmin@gmail.com");
            defaultAdmin.setEmail("yashwanthmp4661@gmail.com");
            defaultAdmin.setPassword(passwordEncoder.encode("Admin@2025"));
            defaultAdmin.setFullName("System Administrator");
            defaultAdmin.setRole(UserRole.PROVIDER_ADMIN);
            defaultAdmin.setIsActive(true);
            defaultAdmin.setIsEnabled(true);
            
            userRepository.save(defaultAdmin);
            
            System.out.println("========================================");
            System.out.println("✅ DEFAULT ADMIN CREATED!");
            System.out.println("📧 Email: superadmin@gmail.com");
            System.out.println("🔐 Password: Admin@2025");
            System.out.println("========================================");
        }
    }
}
