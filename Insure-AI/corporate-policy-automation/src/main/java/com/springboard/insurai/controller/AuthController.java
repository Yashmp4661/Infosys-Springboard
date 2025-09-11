package com.springboard.insurai.controller;


import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import com.springboard.insurai.service.AuthenticationService;
import com.springboard.insurai.service.EmailService;
import com.springboard.insurai.service.ForgotPasswordService;
import com.springboard.insurai.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            if (!request.getUsername().endsWith("@gmail.com")) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Only Gmail addresses are allowed"));
            }

            Optional<User> userOpt = userService.getUserByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
            }

            User user = userOpt.get();
            if (!user.canLogin()) {
                if (!user.getIsEnabled()) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Account not activated. Check your email."));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse("Account disabled."));
            }

            String token = authenticationService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
            if (token == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
            }

            return ResponseEntity.ok(new LoginResponse(token, "Login successful", user.getRole().toString(), user.getFullName(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Login failed: " + e.getMessage()));
        }
    }

    // REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Validate Gmail domain
            if (!request.getEmail().endsWith("@gmail.com")) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Only Gmail addresses are allowed"));
            }

            // Check if user already exists
            Optional<User> existingUserOpt = userService.getUserByUsername(request.getEmail());
            
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();
                
                // Special case: Pre-approved employee setting password for the first time
                if (existingUser.getRole() == UserRole.EMPLOYEE && 
                    existingUser.getPreApproved() == true && 
                    (existingUser.getPassword() == null || existingUser.getPassword().isEmpty())) {
                    
                    // Update the existing user with password and activation token
                    existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    existingUser.setIsActive(true);
                    existingUser.setIsEnabled(false); // Still needs email activation
                    existingUser.setActivationToken(UUID.randomUUID().toString());
                    
                    User updatedUser = userService.updateUser(existingUser);
                    
                    // Send activation email
                    try {
                        emailService.sendActivationEmail(
                            updatedUser.getEmail(),
                            updatedUser.getFullName(),
                            updatedUser.getActivationToken(),
                            updatedUser.getRole().toString()
                        );
                    } catch (Exception e) {
                        System.err.println("⚠️ Failed to send activation email: " + e.getMessage());
                    }
                    
                    return ResponseEntity.ok(new RegisterResponse(
                        "Registration successful! Please check your email to activate your account.",
                        updatedUser.getId(),
                        updatedUser.getRole().toString(),
                        updatedUser.getEmail()
                    ));
                } else {
                    // User exists and is not a pre-approved employee
                    return ResponseEntity.badRequest().body(new ErrorResponse("User with this email already exists"));
                }
            }

            // Block PROVIDER_ADMIN registration via public endpoint
            if (request.getRole() == UserRole.PROVIDER_ADMIN) {
                return ResponseEntity.status(403).body(new ErrorResponse(
                    "Provider Admin accounts can only be created by existing administrators"));
            }

            // For new employee registration (shouldn't happen now, but keeping for safety)
            if (request.getRole() == UserRole.EMPLOYEE) {
                return ResponseEntity.status(403).body(new ErrorResponse(
                    "Employee registration requires pre-approval by Corporate HR"));
            }

            // Normal registration for Corporate Admin
            User user = new User();
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());
            user.setRole(request.getRole());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsActive(true);
            user.setIsEnabled(false); // Need email activation
            user.setActivationToken(UUID.randomUUID().toString());

            User savedUser = userService.createUser(user);

            // Send activation email
            try {
                emailService.sendActivationEmail(
                    savedUser.getEmail(),
                    savedUser.getFullName(),
                    savedUser.getActivationToken(),
                    savedUser.getRole().toString()
                );
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send activation email: " + e.getMessage());
            }

            return ResponseEntity.ok(new RegisterResponse(
                "Registration successful! Please check your email to activate your account.",
                savedUser.getId(),
                savedUser.getRole().toString(),
                savedUser.getEmail()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    

    // ADMIN CREATION
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request, Authentication authentication) {
        try {
            if (userService.getUserByUsername(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin already exists"));
            }

            User admin = new User();
            admin.setUsername(request.getEmail());
            admin.setEmail(request.getEmail());
            admin.setFullName(request.getFullName());
            admin.setRole(UserRole.PROVIDER_ADMIN);
            String pass = request.getPassword();
            admin.setPassword(passwordEncoder.encode(pass));
            admin.setIsActive(true);
            admin.setIsEnabled(true);

            User savedAdmin = userService.createUser(admin);

            String creatorName = userService.getUserByUsername(authentication.getName()).map(User::getFullName).orElse("System Administrator");

            try {
                emailService.sendAdminCreationNotification(savedAdmin.getEmail(), savedAdmin.getFullName(), creatorName,pass);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send admin creation email: " + e.getMessage());
            }

            return ResponseEntity.ok(new RegisterResponse("Admin account created successfully", savedAdmin.getId(), savedAdmin.getRole().toString(), savedAdmin.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create admin: " + e.getMessage()));
        }
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
    }

//    // FORGOT PASSWORD
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
//        try {
//            if (!request.getEmail().endsWith("@gmail.com")) {
//                return ResponseEntity.badRequest().body(new ErrorResponse("Only Gmail addresses are allowed"));
//            }
//
//            forgotPasswordService.createPasswordResetTokenForUser(request.getEmail());
//            return ResponseEntity.ok(new SuccessResponse("If an account with that email exists, we've sent you a password reset link."));
//
//        } catch (Exception e) {
//            return ResponseEntity.ok(new SuccessResponse("If an account with that email exists, we've sent you a password reset link."));
//        }
//    }
    
 // FORGOT PASSWORD - Enhanced version
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String email = request.getEmail().toLowerCase().trim();
            
            // Keep Gmail restriction as requested
            if (!email.endsWith("@gmail.com")) {
                System.out.println("⚠️ Non-Gmail email rejected: " + email);
                return ResponseEntity.badRequest().body(new ErrorResponse("Only Gmail addresses are allowed"));
            }
            
            System.out.println("🔍 Processing forgot password for Gmail: " + email);
            
            // Validate email format more strictly
            if (!isValidGmailFormat(email)) {
                System.out.println("⚠️ Invalid Gmail format: " + email);
                return ResponseEntity.badRequest().body(new ErrorResponse("Please enter a valid Gmail address"));
            }
            
            // Check if user exists
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                System.out.println("⚠️ User not found: " + email);
                // Return generic success message for security
                return ResponseEntity.ok(new SuccessResponse("If an account with that email exists, we've sent you a password reset link."));
            }
            
            User user = userOpt.get();
            System.out.println("✅ User found: " + user.getFullName() + " (ID: " + user.getId() + ")");
            
            // Check if account is enabled
            if (!user.getIsEnabled()) {
                System.out.println("⚠️ Account not enabled: " + email);
                return ResponseEntity.badRequest().body(new ErrorResponse("Please activate your account first by clicking the activation link in your email."));
            }
            
            // Generate and send reset token
            forgotPasswordService.createPasswordResetTokenForUser(email);
            
            return ResponseEntity.ok(new SuccessResponse("Password reset instructions have been sent to your Gmail address. Please check your inbox and spam folder."));
            
        } catch (Exception e) {
            System.err.println("❌ Forgot password error for " + request.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
            
            // Check if it's an email sending failure
            if (e.getMessage().contains("Failed to send password reset email")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ErrorResponse("Unable to send email at this time. Please try again in a few minutes."));
            }
            
            // Generic success message for other errors (security)
            return ResponseEntity.ok(new SuccessResponse("If an account with that email exists, we've sent you a password reset link."));
        }
    }

    // Helper method to validate Gmail format
    private boolean isValidGmailFormat(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    }


    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Passwords do not match"));
            }

            forgotPasswordService.updatePassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new SuccessResponse("Password updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

 
    
 // Account Activation Endpoint
    @GetMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestParam String token) {
        try {
            Optional<User> userOpt = userService.getUserByActivationToken(token);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid or expired activation token"));
            }

            User user = userOpt.get();
            user.setIsEnabled(true);
            user.setActivationToken(null);
            user.setActivatedAt(LocalDateTime.now());
            userService.updateUser(user);

            // Send welcome email
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), user.getRole().toString());
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send welcome email: " + e.getMessage());
            }

            return ResponseEntity.ok(new ActivationResponse("Account activated successfully! You can now log in.", user.getEmail(), user.getFullName()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Activation failed: " + e.getMessage()));
        }
    }

    // Validate Reset Token (for frontend to check if token is valid before showing form)
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            boolean isValid = forgotPasswordService.isValidToken(token);
            if (isValid) {
                return ResponseEntity.ok(new SuccessResponse("Token is valid"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid token"));
        }
    }

    // DTO Classes
    public static class ActivationResponse {
        private String message;
        private String email;
        private String fullName;

        public ActivationResponse(String message, String email, String fullName) {
            this.message = message;
            this.email = email;
            this.fullName = fullName;
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }


    // DTO CLASSES
    public static class LoginRequest {
        @NotBlank @Email
        private String username;
        @NotBlank
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private String message;
        private String role;
        private String fullName;
        private String email;

        public LoginResponse(String token, String message, String role, String fullName, String email) {
            this.token = token;
            this.message = message;
            this.role = role;
            this.fullName = fullName;
            this.email = email;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class RegisterRequest {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        @NotBlank @Size(min = 2, max = 100)
        private String fullName;
        @NotNull
        private UserRole role;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }
    }

    public static class RegisterResponse {
        private String message;
        private Long userId;
        private String role;
        private String email;

        public RegisterResponse(String message, Long userId, String role, String email) {
            this.message = message;
            this.userId = userId;
            this.role = role;
            this.email = email;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        @NotBlank
        private String token;
        @NotBlank @Size(min = 6)
        private String newPassword;
        @NotBlank
        private String confirmPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class CreateAdminRequest {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6)
        private String password;
        @NotBlank @Size(min = 2, max = 100)
        private String fullName;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
