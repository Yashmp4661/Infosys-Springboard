package com.springboard.insurai.repository;


import com.springboard.insurai.model.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorporateRepository extends JpaRepository<Corporate, Long> {
    
    Optional<Corporate> findByEmail(String email);
    Optional<Corporate> findByRegistrationNumber(String registrationNumber);
    List<Corporate> findByIsActiveTrue();
    Optional<Corporate> findByAdminUserId(Long adminUserId);
    Boolean existsByEmail(String email);
    Boolean existsByRegistrationNumber(String registrationNumber);
}

