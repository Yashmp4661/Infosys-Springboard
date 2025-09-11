package com.springboard.insurai.model;



import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;  // Links to User table
    
    @Column(name = "policy_id", nullable = false)
    private Long policyId;   // Links to InsuranceProduct table
    
    @Column(name = "amount_requested", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountRequested;
    
    @Column(name = "claim_documents", columnDefinition = "TEXT")
    private String claimDocuments;  // JSON array of document URLs or comma-separated
    
    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false)
    private ClaimStatus claimStatus = ClaimStatus.SUBMITTED;
    
    @Column(name = "request_date")
    private LocalDateTime requestDate = LocalDateTime.now();
    
    @Column(name = "decision_date")
    private LocalDateTime decisionDate;
    
    @Column(name = "approved_amount", precision = 12, scale = 2)
    private BigDecimal approvedAmount;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "claim_description", columnDefinition = "TEXT")
    private String claimDescription;
    
    @Column(name = "hospital_name")
    private String hospitalName;
    
    @Column(name = "treatment_date")
    private LocalDateTime treatmentDate;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "reviewed_by")
    private Long reviewedBy;  // Who approved/rejected
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Default constructor
    public Claim() {}
    
    // Constructor with essential fields
    public Claim(Long employeeId, Long policyId, BigDecimal amountRequested, String claimDescription) {
        this.employeeId = employeeId;
        this.policyId = policyId;
        this.amountRequested = amountRequested;
        this.claimDescription = claimDescription;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    
    public BigDecimal getAmountRequested() { return amountRequested; }
    public void setAmountRequested(BigDecimal amountRequested) { this.amountRequested = amountRequested; }
    
    public String getClaimDocuments() { return claimDocuments; }
    public void setClaimDocuments(String claimDocuments) { this.claimDocuments = claimDocuments; }
    
    public ClaimStatus getClaimStatus() { return claimStatus; }
    public void setClaimStatus(ClaimStatus claimStatus) { this.claimStatus = claimStatus; }
    
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    
    public LocalDateTime getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDateTime decisionDate) { this.decisionDate = decisionDate; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getClaimDescription() { return claimDescription; }
    public void setClaimDescription(String claimDescription) { this.claimDescription = claimDescription; }
    
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    
    public LocalDateTime getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(LocalDateTime treatmentDate) { this.treatmentDate = treatmentDate; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Update timestamp before saving
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", policyId=" + policyId +
                ", amountRequested=" + amountRequested +
                ", claimStatus=" + claimStatus +
                ", requestDate=" + requestDate +
                '}';
    }
}
