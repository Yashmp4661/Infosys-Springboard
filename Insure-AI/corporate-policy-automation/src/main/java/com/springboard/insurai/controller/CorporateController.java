package com.springboard.insurai.controller;




import com.springboard.insurai.model.User;
import com.springboard.insurai.model.UserRole;
import com.springboard.insurai.service.EmailService;
import com.springboard.insurai.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corporate")
public class CorporateController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // Add employee for pre-approval (only Corporate Admin can do this)
    @PostMapping("/employees/add")
    @PreAuthorize("hasRole('CORPORATE_ADMIN')")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody AddEmployeeRequest request, 
                                       Authentication authentication) {
        try {
            String corporateEmail = authentication.getName();
            Optional<User> corporateAdminOpt = userService.getUserByUsername(corporateEmail);
            
            if (corporateAdminOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Corporate Admin not found"));
            }
            
            User corporateAdmin = corporateAdminOpt.get();
            
            // Validate Gmail domain
            if (!request.getEmail().endsWith("@gmail.com")) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Only Gmail addresses are allowed"));
            }
            
            // Check if employee already exists
            if (userService.getUserByUsername(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Employee with this email already exists"));
            }
            
            // Create pre-approved employee entry WITHOUT password
            User employee = new User();
            employee.setUsername(request.getEmail());
            employee.setEmail(request.getEmail());
            employee.setFullName(request.getFullName());
            employee.setRole(UserRole.EMPLOYEE);
            employee.setPreApproved(true);
            employee.setCorporateAdminId(corporateAdmin.getId());
            employee.setPassword(""); // Empty password - will be set during registration
            employee.setIsActive(false); // Will be activated after registration
            employee.setIsEnabled(false); // Will be enabled after email verification
            
            User savedEmployee = userService.createUser(employee);
            
            // Send invitation email to employee
            try {
                emailService.sendEmployeeInvitationEmail(
                    savedEmployee.getEmail(),
                    savedEmployee.getFullName(),
                    corporateAdmin.getFullName()
                );
            } catch (Exception e) {
                System.err.println("⚠️ Failed to send invitation email: " + e.getMessage());
            }
            
            return ResponseEntity.ok(new SuccessResponse(
                "Employee " + request.getFullName() + " has been added and invitation sent to " + request.getEmail()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to add employee: " + e.getMessage()));
        }
    }

    // Get all employees managed by this corporate admin
    @GetMapping("/employees")
    @PreAuthorize("hasRole('CORPORATE_ADMIN')")
    public ResponseEntity<?> getMyEmployees(Authentication authentication) {
        try {
            String corporateEmail = authentication.getName();
            Optional<User> corporateAdminOpt = userService.getUserByUsername(corporateEmail);
            
            if (corporateAdminOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Corporate Admin not found"));
            }
            
            List<User> employees = userService.getEmployeesByCorporateAdmin(corporateAdminOpt.get().getId());
            return ResponseEntity.ok(employees);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch employees: " + e.getMessage()));
        }
    }
    
    // Activate/Deactivate employee
    @PutMapping("/employees/{employeeId}/status")
    @PreAuthorize("hasRole('CORPORATE_ADMIN')")
    public ResponseEntity<?> updateEmployeeStatus(@PathVariable Long employeeId, 
                                                @RequestBody EmployeeStatusRequest request,
                                                Authentication authentication) {
        try {
            String corporateEmail = authentication.getName();
            Optional<User> corporateAdminOpt = userService.getUserByUsername(corporateEmail);
            
            if (corporateAdminOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Corporate Admin not found"));
            }
            
            Optional<User> employeeOpt = userService.getUserById(employeeId);
            if (employeeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Employee not found"));
            }
            
            User employee = employeeOpt.get();
            
            // Check if this employee belongs to current corporate admin
            if (!employee.getCorporateAdminId().equals(corporateAdminOpt.get().getId())) {
                return ResponseEntity.status(403).body(new ErrorResponse("You can only manage your own employees"));
            }
            
            employee.setIsActive(request.isActive());
            userService.updateUser(employee);
            
            String status = request.isActive() ? "activated" : "deactivated";
            return ResponseEntity.ok(new SuccessResponse("Employee " + status + " successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update employee status: " + e.getMessage()));
        }
    }
    
    // DTO Classes
    public static class AddEmployeeRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;
        
        @NotBlank(message = "Full name is required")
        private String fullName;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }
    
    public static class EmployeeStatusRequest {
        private boolean active;
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
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
