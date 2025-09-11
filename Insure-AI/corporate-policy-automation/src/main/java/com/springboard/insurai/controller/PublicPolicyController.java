package com.springboard.insurai.controller;



import com.springboard.insurai.model.InsuranceProduct;
import com.springboard.insurai.service.InsuranceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicPolicyController {
    
    @Autowired
    private InsuranceProductService insuranceProductService;
    
    // Get all available policies for public display
    @GetMapping("/policies")
    public ResponseEntity<List<PolicyDisplayResponse>> getAllPublicPolicies() {
        List<InsuranceProduct> policies = insuranceProductService.getAllActivePolicies();
        
        List<PolicyDisplayResponse> displayPolicies = policies.stream()
                .map(this::convertToDisplayResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(displayPolicies);
    }
    
    // Get policy details by ID (public view)
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<?> getPolicyDetails(@PathVariable Long policyId) {
        try {
            InsuranceProduct policy = insuranceProductService.getPolicyById(policyId)
                    .orElseThrow(() -> new RuntimeException("Policy not found"));
            
            if (!policy.getIsActive()) {
                return ResponseEntity.notFound().build();
            }
            
            PolicyDetailResponse detailResponse = convertToDetailResponse(policy);
            return ResponseEntity.ok(detailResponse);
            
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get policies by category (Health, Life, Accident, etc.)
    @GetMapping("/policies/category")
    public ResponseEntity<Map<String, List<PolicyDisplayResponse>>> getPoliciesByCategory() {
        List<InsuranceProduct> policies = insuranceProductService.getAllActivePolicies();
        
        Map<String, List<PolicyDisplayResponse>> categorizedPolicies = policies.stream()
                .map(this::convertToDisplayResponse)
                .collect(Collectors.groupingBy(this::categorizePolicy));
        
        return ResponseEntity.ok(categorizedPolicies);
    }
    
    // Search policies by name or benefits
    @GetMapping("/policies/search")
    public ResponseEntity<List<PolicyDisplayResponse>> searchPolicies(@RequestParam String query) {
        List<InsuranceProduct> policies = insuranceProductService.searchPolicies(query);
        
        List<PolicyDisplayResponse> searchResults = policies.stream()
                .map(this::convertToDisplayResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(searchResults);
    }
    
    // Get policy statistics for home page
    @GetMapping("/stats")
    public ResponseEntity<PolicyStatsResponse> getPolicyStats() {
        List<InsuranceProduct> policies = insuranceProductService.getAllActivePolicies();
        
        long totalPolicies = policies.size();
        
        BigDecimal minCoverage = policies.stream()
                .map(InsuranceProduct::getCoverageAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal maxCoverage = policies.stream()
                .map(InsuranceProduct::getCoverageAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal minPremium = policies.stream()
                .map(InsuranceProduct::getPremiumPerEmployee)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        PolicyStatsResponse stats = new PolicyStatsResponse(
                totalPolicies,
                minCoverage,
                maxCoverage,
                minPremium,
                "Contact your HR department to enroll in any of these policies"
        );
        
        return ResponseEntity.ok(stats);
    }
    
    // Helper methods to convert entities to DTOs
    private PolicyDisplayResponse convertToDisplayResponse(InsuranceProduct policy) {
        return new PolicyDisplayResponse(
                policy.getId(),
                policy.getName(),
                policy.getDescription(),
                policy.getCoverageAmount(),
                policy.getPremiumPerEmployee(),
                policy.getPolicyDurationMonths(),
                extractShortBenefits(policy.getBenefits()),
                "Contact your HR for enrollment"
        );
    }
    
    private PolicyDetailResponse convertToDetailResponse(InsuranceProduct policy) {
        return new PolicyDetailResponse(
                policy.getId(),
                policy.getName(),
                policy.getDescription(),
                policy.getCoverageAmount(),
                policy.getPremiumPerEmployee(),
                policy.getPolicyDurationMonths(),
                policy.getBenefits(),
                policy.getExclusions(),
                "Contact your company's HR department to enroll in this policy",
                "This policy is managed by your corporate insurance provider"
        );
    }
    
    private String categorizePolicy(PolicyDisplayResponse policy) {
        String name = policy.getName().toLowerCase();
        if (name.contains("health") || name.contains("medical")) {
            return "Health Insurance";
        } else if (name.contains("life") || name.contains("term")) {
            return "Life Insurance";
        } else if (name.contains("accident") || name.contains("disability")) {
            return "Accident Insurance";
        } else {
            return "General Insurance";
        }
    }
    
    private String extractShortBenefits(String fullBenefits) {
        if (fullBenefits == null || fullBenefits.length() <= 100) {
            return fullBenefits;
        }
        return fullBenefits.substring(0, 97) + "...";
    }
    
    // DTO Classes for Public Display
    public static class PolicyDisplayResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal coverageAmount;
        private BigDecimal premiumPerEmployee;
        private Integer durationMonths;
        private String keyBenefits;
        private String enrollmentInfo;
        
        public PolicyDisplayResponse(Long id, String name, String description, 
                                   BigDecimal coverageAmount, BigDecimal premiumPerEmployee,
                                   Integer durationMonths, String keyBenefits, String enrollmentInfo) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.coverageAmount = coverageAmount;
            this.premiumPerEmployee = premiumPerEmployee;
            this.durationMonths = durationMonths;
            this.keyBenefits = keyBenefits;
            this.enrollmentInfo = enrollmentInfo;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getCoverageAmount() { return coverageAmount; }
        public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
        
        public BigDecimal getPremiumPerEmployee() { return premiumPerEmployee; }
        public void setPremiumPerEmployee(BigDecimal premiumPerEmployee) { this.premiumPerEmployee = premiumPerEmployee; }
        
        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
        
        public String getKeyBenefits() { return keyBenefits; }
        public void setKeyBenefits(String keyBenefits) { this.keyBenefits = keyBenefits; }
        
        public String getEnrollmentInfo() { return enrollmentInfo; }
        public void setEnrollmentInfo(String enrollmentInfo) { this.enrollmentInfo = enrollmentInfo; }
    }
    
    public static class PolicyDetailResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal coverageAmount;
        private BigDecimal premiumPerEmployee;
        private Integer durationMonths;
        private String benefits;
        private String exclusions;
        private String enrollmentInstructions;
        private String additionalInfo;
        
        public PolicyDetailResponse(Long id, String name, String description,
                                  BigDecimal coverageAmount, BigDecimal premiumPerEmployee,
                                  Integer durationMonths, String benefits, String exclusions,
                                  String enrollmentInstructions, String additionalInfo) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.coverageAmount = coverageAmount;
            this.premiumPerEmployee = premiumPerEmployee;
            this.durationMonths = durationMonths;
            this.benefits = benefits;
            this.exclusions = exclusions;
            this.enrollmentInstructions = enrollmentInstructions;
            this.additionalInfo = additionalInfo;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getCoverageAmount() { return coverageAmount; }
        public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }
        
        public BigDecimal getPremiumPerEmployee() { return premiumPerEmployee; }
        public void setPremiumPerEmployee(BigDecimal premiumPerEmployee) { this.premiumPerEmployee = premiumPerEmployee; }
        
        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
        
        public String getBenefits() { return benefits; }
        public void setBenefits(String benefits) { this.benefits = benefits; }
        
        public String getExclusions() { return exclusions; }
        public void setExclusions(String exclusions) { this.exclusions = exclusions; }
        
        public String getEnrollmentInstructions() { return enrollmentInstructions; }
        public void setEnrollmentInstructions(String enrollmentInstructions) { this.enrollmentInstructions = enrollmentInstructions; }
        
        public String getAdditionalInfo() { return additionalInfo; }
        public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    }
    
    public static class PolicyStatsResponse {
        private long totalAvailablePolicies;
        private BigDecimal minCoverageAmount;
        private BigDecimal maxCoverageAmount;
        private BigDecimal startingPremium;
        private String enrollmentMessage;
        
        public PolicyStatsResponse(long totalAvailablePolicies, BigDecimal minCoverageAmount,
                                 BigDecimal maxCoverageAmount, BigDecimal startingPremium,
                                 String enrollmentMessage) {
            this.totalAvailablePolicies = totalAvailablePolicies;
            this.minCoverageAmount = minCoverageAmount;
            this.maxCoverageAmount = maxCoverageAmount;
            this.startingPremium = startingPremium;
            this.enrollmentMessage = enrollmentMessage;
        }
        
        // Getters and setters
        public long getTotalAvailablePolicies() { return totalAvailablePolicies; }
        public void setTotalAvailablePolicies(long totalAvailablePolicies) { this.totalAvailablePolicies = totalAvailablePolicies; }
        
        public BigDecimal getMinCoverageAmount() { return minCoverageAmount; }
        public void setMinCoverageAmount(BigDecimal minCoverageAmount) { this.minCoverageAmount = minCoverageAmount; }
        
        public BigDecimal getMaxCoverageAmount() { return maxCoverageAmount; }
        public void setMaxCoverageAmount(BigDecimal maxCoverageAmount) { this.maxCoverageAmount = maxCoverageAmount; }
        
        public BigDecimal getStartingPremium() { return startingPremium; }
        public void setStartingPremium(BigDecimal startingPremium) { this.startingPremium = startingPremium; }
        
        public String getEnrollmentMessage() { return enrollmentMessage; }
        public void setEnrollmentMessage(String enrollmentMessage) { this.enrollmentMessage = enrollmentMessage; }
    }
}

