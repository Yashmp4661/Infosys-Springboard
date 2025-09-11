import React, { useState } from 'react';
import api from '../../utils/api';
import './Modal.css';

function AddAdminModal({ onClose, onAdminAdded }) {
  const [formData, setFormData] = useState({
    email: '',
    fullName: '',
    password: '',
    confirmPassword: ''
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
    if (!formData.fullName.trim()) {
      setError('Full name is required');
      return;
    }

    if (!formData.email.trim()) {
      setError('Email is required');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return;
    }

    if (!formData.password) {
      setError('Password is required');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    try {
      // Using your exact backend endpoint
      const response = await api.post('/auth/create-admin', {
        email: formData.email.toLowerCase().trim(),
        fullName: formData.fullName.trim(),
        password: formData.password
      });

      setSuccess('Administrator created successfully! Email notification sent.');
      
      // Reset form
      setFormData({
        email: '',
        fullName: '',
        password: '',
        confirmPassword: ''
      });

      // Close modal after 2 seconds
      setTimeout(() => {
        onAdminAdded();
      }, 2000);

    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Failed to create administrator. Please try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Add New Administrator</h2>
          <button className="close-btn" onClick={onClose}>&times;</button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label>Full Name *</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Enter administrator's full name"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Email Address *</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter administrator's email"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Password *</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Create password (min 6 characters)"
              minLength="6"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Confirm Password *</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Confirm password"
              minLength="6"
              required
              disabled={loading}
            />
          </div>

          <div className="admin-info-box">
            <h4>🔐 Administrator Privileges</h4>
            <ul>
              <li>Full system access and control</li>
              <li>Can manage all users and policies</li>
              <li>Access to all system reports and analytics</li>
              <li>Will receive login credentials via email</li>
            </ul>
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
              {loading ? 'Creating Administrator...' : success ? 'Administrator Created!' : 'Create Administrator'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddAdminModal;
