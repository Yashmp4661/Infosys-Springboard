package com.springboard.insurai.service;



import com.springboard.insurai.model.PasswordResetToken;
import com.springboard.insurai.model.User;
import com.springboard.insurai.repository.PasswordResetTokenRepository;
import com.springboard.insurai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;


import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ForgotPasswordService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
//    public void createPasswordResetTokenForUser(String email) {
//        Optional<User> userOpt = userRepository.findByEmail(email);
//        
//        if (userOpt.isEmpty()) {
//            throw new RuntimeException("User not found with email: " + email);
//        }
//        
//        User user = userOpt.get();
//        passwordResetTokenRepository.deleteByUser(user);
//        
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken resetToken = new PasswordResetToken(token, user);
//        passwordResetTokenRepository.save(resetToken);
//        
//        // Send password reset email
//        try {
//            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
//            System.out.println("✅ Password reset email sent to: " + user.getEmail());
//        } catch (Exception e) {
//            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
//            throw new RuntimeException("Failed to send password reset email");
//        }
//    }
    
    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        System.out.println("🔍 Processing password reset for: " + normalizedEmail);
        
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            System.err.println("❌ User not found: " + normalizedEmail);
            throw new RuntimeException("User not found with email: " + normalizedEmail);
        }
        
        User user = userOpt.get();
        System.out.println("👤 User found: " + user.getFullName() + " (ID: " + user.getId() + ")");
        
        // ⭐ CRITICAL FIX: Delete existing tokens FIRST with explicit flush
        try {
            System.out.println("🗑️ Deleting existing tokens for user ID: " + user.getId());
            passwordResetTokenRepository.deleteByUser(user);
            passwordResetTokenRepository.flush(); // Force immediate deletion
            System.out.println("✅ Existing tokens deleted successfully");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Error deleting existing tokens: " + e.getMessage());
            // Continue anyway - might not have existing tokens
        }
        
        // Generate unique token
        String token = UUID.randomUUID().toString();
        System.out.println("🎫 Generated new token: " + token.substring(0, 8) + "...");
        
        // Create new token with retry logic
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        
        try {
            passwordResetTokenRepository.save(resetToken);
            System.out.println("✅ New token saved successfully for user: " + user.getEmail());
        } catch (DataIntegrityViolationException e) {
            System.err.println("❌ Duplicate key error during token creation: " + e.getMessage());
            
            // Retry once after cleaning up more aggressively
            try {
                System.out.println("🔄 Retrying after aggressive cleanup...");
                passwordResetTokenRepository.deleteByUserId(user.getId()); // Use direct query
                Thread.sleep(100); // Small delay
                
                String newToken = UUID.randomUUID().toString(); // Generate fresh token
                PasswordResetToken retryToken = new PasswordResetToken(newToken, user);
                passwordResetTokenRepository.save(retryToken);
                
                token = newToken; // Update for email
                System.out.println("✅ Retry successful with new token");
            } catch (Exception retryError) {
                System.err.println("❌ Retry failed: " + retryError.getMessage());
                throw new RuntimeException("Failed to create password reset token after retry", retryError);
            }
        }
        
        // Send password reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
            System.out.println("✅ Password reset email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    
    public void updatePassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        User user = tokenOpt.get().getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(tokenOpt.get());
        
        // Send confirmation email
        try {
            emailService.sendSimpleMessage(
                user.getEmail(),
                "Password Changed Successfully",
                "Hi " + user.getFullName() + ",\n\nYour password has been successfully changed.\n\nBest regards,\nInsurance Portal Team"
            );
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send confirmation email: " + e.getMessage());
        }
    }
    
    public boolean isValidToken(String token) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        return tokenOpt.isPresent() && !tokenOpt.get().isExpired();
    }
}

