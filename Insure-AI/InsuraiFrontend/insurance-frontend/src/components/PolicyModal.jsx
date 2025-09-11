import React from 'react';
import './PolicyModal.css';

function PolicyModal({ policy, isOpen, onClose }) {
  if (!isOpen || !policy) return null;

  // Handle both PolicyDisplayResponse and PolicyDetailResponse
  const isDetailedView = policy.benefits || policy.exclusions;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{policy.name || 'Policy Details'}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>
        
        <div className="modal-body">
          <div className="policy-detail-item">
            <strong>Policy ID:</strong>
            <span>{policy.id}</span>
          </div>
          
          <div className="policy-detail-item">
            <strong>Description:</strong>
            <p>{policy.description || 'No description available'}</p>
          </div>
          
          <div className="policy-detail-item">
            <strong>Coverage Amount:</strong>
            <span className="amount">${policy.coverageAmount?.toLocaleString() || '0'}</span>
          </div>
          
          <div className="policy-detail-item">
            <strong>Premium per Employee:</strong>
            <span className="amount">${policy.premiumPerEmployee?.toLocaleString() || '0'}</span>
          </div>
          
          <div className="policy-detail-item">
            <strong>Policy Duration:</strong>
            <span>{policy.durationMonths || 12} months</span>
          </div>

          {/* Show detailed benefits if available (from PolicyDetailResponse) */}
          {isDetailedView && (
            <>
              {policy.benefits && (
                <div className="policy-detail-item">
                  <strong>Benefits:</strong>
                  <p>{policy.benefits}</p>
                </div>
              )}
              
              {policy.exclusions && (
                <div className="policy-detail-item">
                  <strong>Exclusions:</strong>
                  <p>{policy.exclusions}</p>
                </div>
              )}
              
              {policy.enrollmentInstructions && (
                <div className="policy-detail-item">
                  <strong>Enrollment Instructions:</strong>
                  <p>{policy.enrollmentInstructions}</p>
                </div>
              )}
              
              {policy.additionalInfo && (
                <div className="policy-detail-item">
                  <strong>Additional Information:</strong>
                  <p>{policy.additionalInfo}</p>
                </div>
              )}
            </>
          )}

          {/* Show basic benefits if available (from PolicyDisplayResponse) */}
          {!isDetailedView && policy.keyBenefits && (
            <div className="policy-detail-item">
              <strong>Key Benefits:</strong>
              <p>{policy.keyBenefits}</p>
            </div>
          )}

          {policy.enrollmentInfo && (
            <div className="policy-detail-item enrollment-info">
              <strong>📞 Enrollment Info:</strong>
              <p>{policy.enrollmentInfo}</p>
            </div>
          )}
        </div>
        
        <div className="modal-footer">
          <button className="btn-secondary" onClick={onClose}>Close</button>
          <button className="btn-primary">Contact HR</button>
        </div>
      </div>
    </div>
  );
}

export default PolicyModal;
