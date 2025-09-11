package com.springboard.insurai.model;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // NEW FIELDS for activation
    @Column(name = "is_enabled")
    private Boolean isEnabled = false;
    
    @Column(name = "activation_token")
    private String activationToken;
    
    @Column(name = "pre_approved")
    private Boolean preApproved = false;
    
    @Column(name = "corporate_admin_id")
    private Long corporateAdminId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String email, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
        this.isEnabled = (role == UserRole.PROVIDER_ADMIN);
    }
    
    // All getters and setters (add the new ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    
    public String getActivationToken() { return activationToken; }
    public void setActivationToken(String activationToken) { this.activationToken = activationToken; }
    
    public Boolean getPreApproved() { return preApproved; }
    public void setPreApproved(Boolean preApproved) { this.preApproved = preApproved; }
    
    public Long getCorporateAdminId() { return corporateAdminId; }
    public void setCorporateAdminId(Long corporateAdminId) { this.corporateAdminId = corporateAdminId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    
    // Helper method
    public boolean canLogin() {
        return isActive && isEnabled;
    }
}
