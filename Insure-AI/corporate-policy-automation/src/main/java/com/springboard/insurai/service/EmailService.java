package com.springboard.insurai.service;



import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.company.name}")
    private String companyName;
    
    @Value("${app.support.email}")
    private String supportEmail;
    
    // Send simple text email
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            
            System.out.println("✅ Simple email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send simple email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    // Send HTML email
    public void sendHtmlMessage(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setReplyTo(supportEmail);
            
            mailSender.send(message);
            System.out.println("✅ HTML email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send HTML email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
//    // Send password reset email
//    public void sendPasswordResetEmail(String to, String fullName, String resetToken) {
//        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
//        String subject = "Password Reset Request - " + appName;
//        String htmlContent = createPasswordResetEmailTemplate(fullName, resetLink);
//        sendHtmlMessage(to, subject, htmlContent);
//    }
    
 // Enhanced password reset email with retry logic
    public void sendPasswordResetEmail(String to, String fullName, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        String subject = "Password Reset Request - " + appName;
        
        // Log attempt
        System.out.println("📧 Attempting to send password reset email to: " + to);
        System.out.println("🔗 Reset link: " + resetLink);
        
        try {
            String htmlContent = createPasswordResetEmailTemplate(fullName, resetLink);
            sendHtmlMessageWithRetry(to, subject, htmlContent, 3); // 3 retry attempts
            
            System.out.println("✅ Password reset email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ FINAL FAILURE - Password reset email to " + to + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email after retries", e);
        }
    }
    
 // Enhanced HTML email sending with retry logic
    private void sendHtmlMessageWithRetry(String to, String subject, String htmlBody, int maxRetries) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("📧 Email attempt " + attempt + "/" + maxRetries + " to: " + to);
                
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                helper.setReplyTo(supportEmail);
                
                // Add delay between attempts to avoid Gmail rate limiting
                if (attempt > 1) {
                    Thread.sleep(2000 * attempt); // Exponential backoff: 2s, 4s, 6s
                }
                
                mailSender.send(message);
                System.out.println("✅ Email sent successfully on attempt " + attempt + " to: " + to);
                return; // Success - exit retry loop
                
            } catch (MessagingException | InterruptedException e) {
                lastException = e;
                System.err.println("⚠️ Email attempt " + attempt + " failed to " + to + ": " + e.getMessage());
                
                if (attempt == maxRetries) {
                    System.err.println("❌ All " + maxRetries + " attempts failed for: " + to);
                } else {
                    System.out.println("🔄 Retrying in " + (2 * attempt) + " seconds...");
                }
            }
        }
        
        throw new RuntimeException("Failed to send email after " + maxRetries + " attempts", lastException);
    }
    
    
    // Send account activation email
    public void sendActivationEmail(String to, String fullName, String activationToken, String role) {
        String activationLink = frontendUrl + "/activate?token=" + activationToken;
        String subject = "Activate Your Account - " + appName;
        String htmlContent = createActivationEmailTemplate(fullName, activationLink, role);
        sendHtmlMessage(to, subject, htmlContent);
    }
    
    // Send welcome email after activation
    public void sendWelcomeEmail(String to, String fullName, String role) {
        String subject = "Welcome to " + appName + "!";
        String htmlContent = createWelcomeEmailTemplate(fullName, role);
        sendHtmlMessage(to, subject, htmlContent);
    }
    public void sendEmployeeInvitationEmail(String employeeEmail, String employeeName, String corporateAdminName) {
        String subject = "You're Invited to Join " + appName;
        // Add source parameter to help frontend identify employee invitations
        String registrationLink = frontendUrl + "/register?source=employee-invitation&email=" + employeeEmail;
        
        String htmlContent = createEmployeeInvitationTemplate(employeeName, corporateAdminName, registrationLink, employeeEmail);
        sendHtmlMessage(employeeEmail, subject, htmlContent);
    }

   
    // Send admin creation notification
    public void sendAdminCreationNotification(String adminEmail, String adminName, String createdBy, String password) {
        String subject = "Administrator Account Created - " + appName;
        String loginLink = frontendUrl + "/login";
        String htmlContent = createAdminCreationTemplate(adminName, createdBy, loginLink,password);
        sendHtmlMessage(adminEmail, subject, htmlContent);
    }
    
    // ============= EMAIL TEMPLATES =============
    
    private String createPasswordResetEmailTemplate(String fullName, String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Password Reset</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".reset-button { display: inline-block; padding: 12px 30px; background: #28a745; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }" +
                ".reset-button:hover { background: #218838; }" +
                ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; }" +
                ".warning { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 6px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>" + appName + "</h1>" +
                "<h2>🔐 Password Reset Request</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hi " + fullName + ",</p>" +
                "<p>We received a request to reset your password for your " + appName + " account.</p>" +
                "<p>Click the button below to reset your password:</p>" +
                "<div style='text-align: center;'>" +
                "<a href='" + resetLink + "' class='reset-button'>Reset Password</a>" +
                "</div>" +
                "<div class='warning'>" +
                "<p><strong>⚠️ Important:</strong></p>" +
                "<ul>" +
                "<li>This link will expire in 1 hour</li>" +
                "<li>If you didn't request this, please ignore this email</li>" +
                "<li>Never share this link with anyone</li>" +
                "</ul>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Best regards,<br>The " + companyName + " Team</p>" +
                "<p>Need help? Contact us at " + supportEmail + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    private String createActivationEmailTemplate(String fullName, String activationLink, String role) {
        String roleDescription = getRoleDescription(role);
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".activate-button { display: inline-block; padding: 12px 30px; background: #007bff; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }" +
                ".role-badge { display: inline-block; padding: 8px 16px; background: #6f42c1; color: white; border-radius: 20px; font-size: 14px; font-weight: bold; }" +
                ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; }" +
                ".feature-list { background: #f8f9fa; padding: 20px; border-radius: 6px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎉 Welcome to " + appName + "!</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hi " + fullName + ",</p>" +
                "<p>Thank you for registering with " + appName + " as a <span class='role-badge'>" + role + "</span>!</p>" +
                "<div class='feature-list'>" +
                "<p><strong>Your Role:</strong> " + role + "</p>" +
                "<p>" + roleDescription + "</p>" +
                "</div>" +
                "<p>To complete your registration and start using your account, please activate it by clicking the button below:</p>" +
                "<div style='text-align: center;'>" +
                "<a href='" + activationLink + "' class='activate-button'>🚀 Activate My Account</a>" +
                "</div>" +
                "<p><strong>⏰ This activation link expires in 24 hours.</strong></p>" +
                "<p>If you didn't create this account, please ignore this email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Best regards,<br>The " + companyName + " Team</p>" +
                "<p>Questions? Contact us at " + supportEmail + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    private String createWelcomeEmailTemplate(String fullName, String role) {
        String roleDescription = getRoleDescription(role);
        String dashboardLink = frontendUrl + "/dashboard";
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #fd7e14 0%, #e83e8c 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".dashboard-button { display: inline-block; padding: 12px 30px; background: #17a2b8; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }" +
                ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; }" +
                ".success-message { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 6px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎊 Account Activated Successfully!</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='success-message'>" +
                "<p><strong>✅ Your " + appName + " account is now active and ready to use!</strong></p>" +
                "</div>" +
                "<p>Hi " + fullName + ",</p>" +
                "<p>Congratulations! Your account has been successfully activated. You can now access all features available to your role.</p>" +
                "<p><strong>Your Role:</strong> " + role + "</p>" +
                "<p>" + roleDescription + "</p>" +
                "<div style='text-align: center;'>" +
                "<a href='" + dashboardLink + "' class='dashboard-button'>📊 Go to Dashboard</a>" +
                "</div>" +
                "<p>If you have any questions or need assistance, don't hesitate to reach out to our support team.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Best regards,<br>The " + companyName + " Team</p>" +
                "<p>Support: " + supportEmail + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    private String createEmployeeInvitationTemplate(String employeeName, String corporateAdminName, String registrationLink, String employeeEmail) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #6610f2 0%, #6f42c1 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".register-button { display: inline-block; padding: 12px 30px; background: #28a745; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }" +
                ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; }" +
                ".invitation-details { background: #e7f3ff; border-left: 4px solid #007bff; padding: 15px; border-radius: 6px; margin: 20px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎉 You're Invited!</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hi " + employeeName + ",</p>" +
                "<p>Great news! <strong>" + corporateAdminName + "</strong> has invited you to join " + appName + " as an employee.</p>" +
                "<div class='invitation-details'>" +
                "<p><strong>📧 Your Email:</strong> " + employeeEmail + "</p>" +
                "<p><strong>👤 Role:</strong> Employee</p>" +
                "<p><strong>🏢 Invited By:</strong> " + corporateAdminName + "</p>" +
                "</div>" +
                "<p>You can now create your account using this email address. During registration:</p>" +
                "<ul>" +
                "<li>Use the email address: <strong>" + employeeEmail + "</strong></li>" +
                "<li>Create a secure password (minimum 6 characters)</li>" +
                "<li>Select 'Employee' as your role</li>" +
                "<li>Complete the activation process via email</li>" +
                "</ul>" +
                "<div style='text-align: center;'>" +
                "<a href='" + registrationLink + "' class='register-button'>🚀 Register Now</a>" +
                "</div>" +
                "<p>Once registered and activated, you'll have access to:</p>" +
                "<ul>" +
                "<li>View available insurance policies</li>" +
                "<li>Submit and track insurance claims</li>" +
                "<li>Manage your insurance coverage</li>" +
                "<li>Access your policy documents</li>" +
                "</ul>" +
                "<p>If you have any questions, please contact your HR department or our support team.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Best regards,<br>The " + companyName + " Team</p>" +
                "<p>Support: " + supportEmail + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    private String createAdminCreationTemplate(String adminName, String createdBy, String loginLink,String password) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; margin: 0; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); overflow: hidden; }" +
                ".header { background: linear-gradient(135deg, #dc3545 0%, #fd7e14 100%); color: white; padding: 30px; text-align: center; }" +
                ".content { padding: 30px; }" +
                ".login-button { display: inline-block; padding: 12px 30px; background: #007bff; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }" +
                ".footer { background: #f8f9fa; padding: 20px; text-align: center; color: #6c757d; font-size: 14px; }" +
                ".admin-badge { display: inline-block; padding: 8px 16px; background: #dc3545; color: white; border-radius: 20px; font-size: 14px; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>👨‍💼 Administrator Account Created</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hi " + adminName + ",</p>" +
                "<p>Your administrator account has been successfully created in " + appName + " by <strong>" + createdBy + "</strong>.</p>" +
                "<p>Role: <span class='admin-badge'>PROVIDER ADMIN</span></p>" +
                "<p>As a Provider Admin, you have full access to:</p>" +
                "<ul>" +
                "<li>Manage all insurance policies</li>" +
                "<li>Create and manage other administrator accounts</li>" +
                "<li>Oversee corporate clients and their employees</li>" +
                "<li>Review and approve insurance claims</li>" +
                "<li>Access system analytics and reports</li>" +
                "</ul>" +
                "<div style='text-align: center;'>" +
                "<a href='" + loginLink + "' class='login-button'>🔐 Login to Dashboard</a>" +
                "</div>" +
                "<p> Your Password is " +password+ "</p>"+
                "<p><strong>⚠️ Security Reminder:</strong> Please change your password once you login to your account.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Best regards,<br>The " + companyName + " Team</p>" +
                "<p>Support: " + supportEmail + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    private String getRoleDescription(String role) {
        switch (role) {
            case "PROVIDER_ADMIN":
                return "As a Provider Admin, you have full access to manage insurance policies, review claims, oversee corporate clients, and manage the entire system.";
            case "CORPORATE_ADMIN":
                return "As a Corporate Admin, you can manage your company's insurance policies, add/remove employees, handle claims for your organization, and oversee your team's coverage.";
            case "EMPLOYEE":
                return "As an Employee, you can view your insurance coverage, submit and track claims, access policy documents, and manage your personal insurance information.";
            default:
                return "Welcome to our insurance management platform. You now have access to manage your insurance needs efficiently.";
        }
    }
    
    // Test email connectivity
    public boolean testEmailConnection() {
        try {
            sendSimpleMessage(
                fromEmail,
                "Test Email - " + appName,
                "This is a test email to verify email configuration is working correctly.\n\n" +
                        "If you receive this email, your email service is properly configured!\n\n" +
                        "Best regards,\n" + companyName + " Team"
            );
            return true;
        } catch (Exception e) {
            System.err.println("❌ Email connection test failed: " + e.getMessage());
            return false;
        }
    }
    

   
          

}
