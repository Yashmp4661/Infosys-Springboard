import React, { useState } from 'react';
import api from '../../utils/api';
import './Modal.css';

function AddPolicyModal({ onClose, onPolicyAdded }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    coverageAmount: '',
    premiumPerEmployee: '',
    policyDurationMonths: '12',
    benefits: '',
    exclusions: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validation
    if (!formData.name.trim()) {
      setError('Policy name is required');
      return;
    }

    if (formData.name.length < 3 || formData.name.length > 100) {
      setError('Policy name must be between 3 and 100 characters');
      return;
    }

    if (!formData.description.trim()) {
      setError('Policy description is required');
      return;
    }

    if (formData.description.length < 10) {
      setError('Description must be at least 10 characters');
      return;
    }

    if (!formData.coverageAmount || parseFloat(formData.coverageAmount) < 10000) {
      setError('Coverage amount must be at least ₹10,000');
      return;
    }

    if (!formData.premiumPerEmployee || parseFloat(formData.premiumPerEmployee) < 500) {
      setError('Premium per employee must be at least ₹500');
      return;
    }

    if (!formData.benefits.trim()) {
      setError('Benefits are required');
      return;
    }

    if (!formData.exclusions.trim()) {
      setError('Exclusions are required');
      return;
    }

    setLoading(true);

    try {
      const policyData = {
        name: formData.name.trim(),
        description: formData.description.trim(),
        coverageAmount: parseFloat(formData.coverageAmount),
        premiumPerEmployee: parseFloat(formData.premiumPerEmployee),
        policyDurationMonths: parseInt(formData.policyDurationMonths),
        benefits: formData.benefits.trim(),
        exclusions: formData.exclusions.trim()
      };

      const response = await api.post('/provider-admin/policies', policyData);
      setSuccess('Policy created successfully!');
      
      // Reset form
      setFormData({
        name: '',
        description: '',
        coverageAmount: '',
        premiumPerEmployee: '',
        policyDurationMonths: '12',
        benefits: '',
        exclusions: ''
      });

      setTimeout(() => {
        onPolicyAdded();
      }, 2000);

    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Failed to create policy. Please try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content large-modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Create New Insurance Policy</h2>
          <button className="close-btn" onClick={onClose}>&times;</button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-row">
            <div className="form-group">
              <label>Policy Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter policy name (3-100 characters)"
                required
                disabled={loading}
                maxLength="100"
              />
            </div>

            <div className="form-group">
              <label>Duration (Months) *</label>
              <select
                name="policyDurationMonths"
                value={formData.policyDurationMonths}
                onChange={handleChange}
                required
                disabled={loading}
              >
                <option value="6">6 months</option>
                <option value="12">12 months</option>
                <option value="24">24 months</option>
                <option value="36">36 months</option>
                <option value="60">60 months</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Description *</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Enter detailed policy description (min 10 characters)"
              rows="3"
              required
              disabled={loading}
              maxLength="1000"
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Coverage Amount (₹) *</label>
              <input
                type="number"
                name="coverageAmount"
                value={formData.coverageAmount}
                onChange={handleChange}
                placeholder="Minimum ₹10,000"
                min="10000"
                max="10000000"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label>Premium per Employee (₹) *</label>
              <input
                type="number"
                name="premiumPerEmployee"
                value={formData.premiumPerEmployee}
                onChange={handleChange}
                placeholder="Minimum ₹500"
                min="500"
                max="100000"
                required
                disabled={loading}
              />
            </div>
          </div>

          <div className="form-group">
            <label>Benefits *</label>
            <textarea
              name="benefits"
              value={formData.benefits}
              onChange={handleChange}
              placeholder="List the benefits covered by this policy (min 10 characters)"
              rows="4"
              required
              disabled={loading}
              maxLength="2000"
            />
          </div>

          <div className="form-group">
            <label>Exclusions *</label>
            <textarea
              name="exclusions"
              value={formData.exclusions}
              onChange={handleChange}
              placeholder="List what is excluded from this policy (min 10 characters)"
              rows="3"
              required
              disabled={loading}
              maxLength="2000"
            />
          </div>

          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}

          <div className="modal-actions">
            <button 
              type="button" 
              onClick={onClose} 
              className="btn btn-secondary"
              disabled={loading}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              disabled={loading || success} 
              className="btn btn-primary"
            >
              {loading ? 'Creating Policy...' : success ? 'Policy Created!' : 'Create Policy'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddPolicyModal;
