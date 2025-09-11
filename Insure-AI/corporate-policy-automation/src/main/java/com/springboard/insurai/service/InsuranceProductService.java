package com.springboard.insurai.service;



import com.springboard.insurai.model.InsuranceProduct;
import com.springboard.insurai.repository.InsuranceProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InsuranceProductService {
    
    @Autowired
    private InsuranceProductRepository insuranceProductRepository;
    
    // Create new insurance policy (by Provider Admin)
    public InsuranceProduct createPolicy(InsuranceProduct policy) {
        return insuranceProductRepository.save(policy);
    }
    
    // Get all active policies (for public display)
    public List<InsuranceProduct> getAllActivePolicies() {
        return insuranceProductRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    // Get policies created by specific admin
    public List<InsuranceProduct> getPoliciesByCreator(Long creatorId) {
        return insuranceProductRepository.findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(creatorId);
    }
    
    // Get policy by ID
    public Optional<InsuranceProduct> getPolicyById(Long policyId) {
        return insuranceProductRepository.findById(policyId);
    }
    
    // Update policy (only by creator)
    public InsuranceProduct updatePolicy(Long policyId, InsuranceProduct updatedPolicy, Long requesterId) {
        Optional<InsuranceProduct> existingPolicyOpt = insuranceProductRepository.findById(policyId);
        
        if (existingPolicyOpt.isPresent()) {
            InsuranceProduct existingPolicy = existingPolicyOpt.get();
            
            // Check if requester is the creator
            if (!existingPolicy.getCreatedBy().equals(requesterId)) {
                throw new RuntimeException("Only the creator can update this policy");
            }
            
            // Update fields
            existingPolicy.setName(updatedPolicy.getName());
            existingPolicy.setDescription(updatedPolicy.getDescription());
            existingPolicy.setCoverageAmount(updatedPolicy.getCoverageAmount());
            existingPolicy.setPremiumPerEmployee(updatedPolicy.getPremiumPerEmployee());
            existingPolicy.setPolicyDurationMonths(updatedPolicy.getPolicyDurationMonths());
            existingPolicy.setBenefits(updatedPolicy.getBenefits());
            existingPolicy.setExclusions(updatedPolicy.getExclusions());
            
            return insuranceProductRepository.save(existingPolicy);
        } else {
            throw new RuntimeException("Policy not found with ID: " + policyId);
        }
    }
    
    // Deactivate policy
    public void deactivatePolicy(Long policyId, Long requesterId) {
        Optional<InsuranceProduct> policyOpt = insuranceProductRepository.findById(policyId);
        
        if (policyOpt.isPresent()) {
            InsuranceProduct policy = policyOpt.get();
            
            // Check if requester is the creator
            if (!policy.getCreatedBy().equals(requesterId)) {
                throw new RuntimeException("Only the creator can deactivate this policy");
            }
            
            policy.setIsActive(false);
            insuranceProductRepository.save(policy);
        } else {
            throw new RuntimeException("Policy not found with ID: " + policyId);
        }
    }
    public void restorePolicy(Long policyId, Long requesterId) {
        Optional<InsuranceProduct> policyOpt = insuranceProductRepository.findById(policyId);
        
        if (policyOpt.isPresent()) {
            InsuranceProduct policy = policyOpt.get();
            
            // Check if requester is the creator (same security as delete)
            if (!policy.getCreatedBy().equals(requesterId)) {
                throw new RuntimeException("Only the creator can restore this policy");
            }
            
            // Check if policy is actually deactivated
            if (policy.getIsActive()) {
                throw new RuntimeException("Policy is already active");
            }
            
            // Restore the policy
            policy.setIsActive(true);
            insuranceProductRepository.save(policy);
            
        } else {
            throw new RuntimeException("Policy not found with ID: " + policyId);
        }
    }
    
    public List<InsuranceProduct> getInactivePoliciesByCreator(Long creatorId) {
        return insuranceProductRepository.findByCreatedByAndIsActiveFalseOrderByCreatedAtDesc(creatorId);
    }
    // Get total policies count for admin
    public Long getTotalPoliciesCount() {
        return insuranceProductRepository.count();
    }
    
    // Get policies count by creator
    public Long getPoliciesCountByCreator(Long creatorId) {
        return insuranceProductRepository.countPoliciesCreatedByAdmin(creatorId);
    }
    
    // Search policies by name
    public List<InsuranceProduct> searchPolicies(String searchTerm) {
        return insuranceProductRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm);
    }
}

