package com.springboard.insurai.repository;



import com.springboard.insurai.model.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
    
    // Find all active policies
    List<InsuranceProduct> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // Find policies created by specific admin
    List<InsuranceProduct> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    // Find active policies created by specific admin
    List<InsuranceProduct> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(Long createdBy);
    
    // Search policies by name
    List<InsuranceProduct> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // Count policies created by admin
    @Query("SELECT COUNT(ip) FROM InsuranceProduct ip WHERE ip.createdBy = :adminId")
    Long countPoliciesCreatedByAdmin(@Param("adminId") Long adminId);
    
    List<InsuranceProduct> findByCreatedByAndIsActiveFalseOrderByCreatedAtDesc(Long createdBy);
}
