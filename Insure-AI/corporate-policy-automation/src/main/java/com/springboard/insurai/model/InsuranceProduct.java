package com.springboard.insurai.model;



import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_products")
public class InsuranceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;
    
    @Column(name = "premium_per_employee", nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumPerEmployee;
    
    @Column(name = "policy_duration_months")
    private Integer policyDurationMonths = 12;
    
    @Column(columnDefinition = "TEXT")
    private String benefits;
    
    @Column(columnDefinition = "TEXT")
    private String exclusions;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Default constructor
    public InsuranceProduct() {}
    
    // Getters and Setters
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
    
    public Integer getPolicyDurationMonths() { return policyDurationMonths; }
    public void setPolicyDurationMonths(Integer policyDurationMonths) { this.policyDurationMonths = policyDurationMonths; }
    
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    
    public String getExclusions() { return exclusions; }
    public void setExclusions(String exclusions) { this.exclusions = exclusions; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "InsuranceProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverageAmount=" + coverageAmount +
                ", premiumPerEmployee=" + premiumPerEmployee +
                ", isActive=" + isActive +
                ", createdBy=" + createdBy +
                '}';
    }

}
