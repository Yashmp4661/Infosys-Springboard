package com.springboard.insurai.model;


public enum ClaimStatus {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PAYMENT_INITIATED("Payment Initiated"),
    PAID("Paid");
    
    private final String displayName;
    
    ClaimStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

