package com.springboard.insurai.service;



import com.springboard.insurai.model.User;
import com.springboard.insurai.repository.UserRepository;
import com.springboard.insurai.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String authenticateAndGenerateToken(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if password matches and user is active
            if (passwordEncoder.matches(password, user.getPassword()) && user.getIsActive()) {
                // Generate and return JWT token
                return jwtUtil.generateToken(username, user.getRole().name(), user.getId());
            }
        }
        
        return null; // Authentication failed
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
