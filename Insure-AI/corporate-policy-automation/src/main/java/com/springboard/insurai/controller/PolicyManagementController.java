package com.springboard.insurai.controller;



import com.springboard.insurai.model.InsuranceProduct;
import com.springboard.insurai.service.InsuranceProductService;
import com.springboard.insurai.service.UserService;
import com.springboard.insurai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/provider-admin")
public class PolicyManagementController {
    
    @Autowired
    private InsuranceProductService insuranceProductService;
    
    @Autowired
    private UserService userService;
    
    // Create new insurance policy (Provider Admin only)
    @PostMapping("/policies")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> createPolicy(@RequestBody CreatePolicyRequest request, Authentication authentication) {
        try {
            // Get current admin user
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            // Create new insurance product
            InsuranceProduct policy = new InsuranceProduct();
            policy.setName(request.getName());
            policy.setDescription(request.getDescription());
            policy.setCoverageAmount(request.getCoverageAmount());
            policy.setPremiumPerEmployee(request.getPremiumPerEmployee());
            policy.setPolicyDurationMonths(request.getPolicyDurationMonths());
            policy.setBenefits(request.getBenefits());
            policy.setExclusions(request.getExclusions());
            policy.setCreatedBy(adminUser.get().getId());
            
            InsuranceProduct savedPolicy = insuranceProductService.createPolicy(policy);
            
            return ResponseEntity.ok(new PolicyCreationResponse(
                "Policy created successfully", 
                savedPolicy.getId(), 
                savedPolicy.getName(),
                savedPolicy.getCoverageAmount(),
                savedPolicy.getPremiumPerEmployee(),
                "Created by: " + currentUsername
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create policy: " + e.getMessage()));
        }
    }
    
    // Get all policies created by current admin
    @GetMapping("/policies/my-policies")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> getMyPolicies(Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            List<InsuranceProduct> policies = insuranceProductService.getPoliciesByCreator(adminUser.get().getId());
            return ResponseEntity.ok(policies);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch policies: " + e.getMessage()));
        }
    }
    
    // Get all active policies (for admin overview)
    @GetMapping("/policies")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<List<InsuranceProduct>> getAllPolicies() {
        List<InsuranceProduct> policies = insuranceProductService.getAllActivePolicies();
        return ResponseEntity.ok(policies);
    }
    
    // Get specific policy details
    @GetMapping("/policies/{policyId}")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> getPolicyById(@PathVariable Long policyId) {
        Optional<InsuranceProduct> policy = insuranceProductService.getPolicyById(policyId);
        
        if (policy.isPresent()) {
            return ResponseEntity.ok(policy.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
 // Add this helper method to your PolicyManagementController class
    private Long getCurrentAdminId(Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<User> adminUser = userService.getUserByUsername(currentUsername);
        if (adminUser.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }
        return adminUser.get().getId();
    }

    
    // Update policy (only by creator)
    @PutMapping("/policies/{policyId}")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> updatePolicy(@PathVariable Long policyId, 
                                        @Valid @RequestBody CreatePolicyRequest request,
                                        Authentication authentication) {
        
        System.out.println("==== UPDATE POLICY CALLED ====");
        System.out.println("Coverage Amount: " + request.getCoverageAmount());
        System.out.println("Premium: " + request.getPremiumPerEmployee());
        System.out.println("Policy Name: " + request.getName());
        
        try {
            Long adminId = getCurrentAdminId(authentication);
            
            InsuranceProduct updatedPolicyData = new InsuranceProduct();
            updatedPolicyData.setName(request.getName());
            updatedPolicyData.setDescription(request.getDescription());
            updatedPolicyData.setCoverageAmount(request.getCoverageAmount());
            updatedPolicyData.setPremiumPerEmployee(request.getPremiumPerEmployee());
            updatedPolicyData.setPolicyDurationMonths(request.getPolicyDurationMonths());
            updatedPolicyData.setBenefits(request.getBenefits());
            updatedPolicyData.setExclusions(request.getExclusions());
            
            InsuranceProduct updatedPolicy = insuranceProductService.updatePolicy(
                policyId, updatedPolicyData, adminId
            );
            
            System.out.println("==== POLICY UPDATED SUCCESSFULLY ====");
            return ResponseEntity.ok(updatedPolicy);
            
        } catch (Exception e) {
            System.out.println("==== EXCEPTION IN UPDATE: " + e.getMessage() + " ====");
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update policy: " + e.getMessage()));
        }
    }

    
    // Deactivate policy
    @DeleteMapping("/policies/{policyId}")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> deactivatePolicy(@PathVariable Long policyId, Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            insuranceProductService.deactivatePolicy(policyId, adminUser.get().getId());
            
            return ResponseEntity.ok(new SuccessResponse("Policy deactivated successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to deactivate policy: " + e.getMessage()));
        }
    }
    
    // Admin dashboard stats
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            Long myPoliciesCount = insuranceProductService.getPoliciesCountByCreator(adminUser.get().getId());
            Long totalPoliciesCount = insuranceProductService.getTotalPoliciesCount();
            
            return ResponseEntity.ok(new DashboardStatsResponse(
                "Welcome " + currentUsername,
                myPoliciesCount,
                totalPoliciesCount,
                adminUser.get().getId()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch dashboard stats: " + e.getMessage()));
        }
    }
    
 // Restore deactivated policy
    @PutMapping("/policies/{policyId}/restore")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> restorePolicy(@PathVariable Long policyId, Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            insuranceProductService.restorePolicy(policyId, adminUser.get().getId());
            
            return ResponseEntity.ok(new SuccessResponse("Policy restored successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to restore policy: " + e.getMessage()));
        }
    }

    // Get inactive policies that can be restored
    @GetMapping("/policies/inactive")
    @PreAuthorize("hasRole('PROVIDER_ADMIN')")
    public ResponseEntity<?> getInactivePolicies(Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            Optional<User> adminUser = userService.getUserByUsername(currentUsername);
            
            if (adminUser.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Admin user not found"));
            }
            
            List<InsuranceProduct> inactivePolicies = insuranceProductService.getInactivePoliciesByCreator(adminUser.get().getId());
            return ResponseEntity.ok(inactivePolicies);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch inactive policies: " + e.getMessage()));
        }
    }
    
    // Request/Response DTOs
    public static class CreatePolicyRequest {
    	@NotBlank(message = "Policy name is required")
        @Size(min = 3, max = 100, message = "Policy name must be between 3 and 100 characters")
        private String name;
        
        @NotBlank(message = "Policy description is required")
        @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
        private String description;
        
        @NotNull(message = "Coverage amount is required")
        @DecimalMin(value = "10000.0", message = "Coverage amount must be at least ₹10,000")
        @DecimalMax(value = "10000000.0", message = "Coverage amount cannot exceed ₹1,00,00,000")
        private BigDecimal coverageAmount;
        
        @NotNull(message = "Premium per employee is required")
        @DecimalMin(value = "500.0", message = "Premium per employee must be at least ₹500")
        @DecimalMax(value = "100000.0", message = "Premium per employee cannot exceed ₹1,00,000")
        private BigDecimal premiumPerEmployee;
        
        @NotNull(message = "Policy duration is required")
        @Min(value = 1, message = "Policy duration must be at least 1 month")
        @Max(value = 120, message = "Policy duration cannot exceed 120 months")
        private Integer policyDurationMonths;
        
        @NotBlank(message = "Benefits are required")
        @Size(min = 10, max = 2000, message = "Benefits must be between 10 and 2000 characters")
        private String benefits;
        
        @NotBlank(message = "Exclusions are required")
        @Size(min = 10, max = 2000, message = "Exclusions must be between 10 and 2000 characters")
        private String exclusions;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getCoverageAmount() { return coverageAmount; }
        public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
        
        public BigDecimal getPremiumPerEmployee() { return premiumPerEmployee; }
        public void setPremiumPerEmployee(BigDecimal premiumPerEmployee) { this.premiumPerEmployee = premiumPerEmployee; }
        
        public Integer getPolicyDurationMonths() { return policyDurationMonths; }
        public void setPolicyDurationMonths(Integer policyDurationMonths) { this.policyDurationMonths = policyDurationMonths; }
        
        public String getBenefits() { return benefits; }
        public void setBenefits(String benefits) { this.benefits = benefits; }
        
        public String getExclusions() { return exclusions; }
        public void setExclusions(String exclusions) { this.exclusions = exclusions; }
    }
    
    public static class PolicyCreationResponse {
        private String message;
        private Long policyId;
        private String policyName;
        private BigDecimal coverageAmount;
        private BigDecimal premiumPerEmployee;
        private String createdBy;
        
        public PolicyCreationResponse(String message, Long policyId, String policyName, 
                                    BigDecimal coverageAmount, BigDecimal premiumPerEmployee, String createdBy) {
            this.message = message;
            this.policyId = policyId;
            this.policyName = policyName;
            this.coverageAmount = coverageAmount;
            this.premiumPerEmployee = premiumPerEmployee;
            this.createdBy = createdBy;
        }
        
        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Long getPolicyId() { return policyId; }
        public void setPolicyId(Long policyId) { this.policyId = policyId; }
        
        public String getPolicyName() { return policyName; }
        public void setPolicyName(String policyName) { this.policyName = policyName; }
        
        public BigDecimal getCoverageAmount() { return coverageAmount; }
        public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
        
        public BigDecimal getPremiumPerEmployee() { return premiumPerEmployee; }
        public void setPremiumPerEmployee(BigDecimal premiumPerEmployee) { this.premiumPerEmployee = premiumPerEmployee; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
    
    public static class DashboardStatsResponse {
        private String welcome;
        private Long myPoliciesCount;
        private Long totalPoliciesCount;
        private Long adminId;
        
        public DashboardStatsResponse(String welcome, Long myPoliciesCount, Long totalPoliciesCount, Long adminId) {
            this.welcome = welcome;
            this.myPoliciesCount = myPoliciesCount;
            this.totalPoliciesCount = totalPoliciesCount;
            this.adminId = adminId;
        }
        
        // Getters and setters
        public String getWelcome() { return welcome; }
        public void setWelcome(String welcome) { this.welcome = welcome; }
        
        public Long getMyPoliciesCount() { return myPoliciesCount; }
        public void setMyPoliciesCount(Long myPoliciesCount) { this.myPoliciesCount = myPoliciesCount; }
        
        public Long getTotalPoliciesCount() { return totalPoliciesCount; }
        public void setTotalPoliciesCount(Long totalPoliciesCount) { this.totalPoliciesCount = totalPoliciesCount; }
        
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
    }
    
    public static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
