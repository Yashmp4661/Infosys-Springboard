import React, { useState } from 'react';
import api from '../../utils/api';
import './Modal.css';

function AddEmployeeModal({ onClose, onEmployeeAdded }) {
  const [formData, setFormData] = useState({
    fullName: '',
    email: ''
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

    // Basic validation
    if (!formData.fullName.trim()) {
      setError('Full name is required');
      return;
    }

    if (!formData.email.trim()) {
      setError('Email is required');
      return;
    }

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return;
    }

    setLoading(true);

    try {
      // Using your exact backend endpoint and request format
      const response = await api.post('/corporate/employees/add', {
        fullName: formData.fullName.trim(),
        email: formData.email.toLowerCase().trim()
      });

      setSuccess(response.data.message || 'Employee added successfully! Invitation email sent.');
      
      // Reset form
      setFormData({ fullName: '', email: '' });
      
      // Close modal after 2 seconds and refresh parent
      setTimeout(() => {
        onEmployeeAdded();
      }, 2000);

    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Failed to add employee. Please try again.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Add New Employee</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label>Full Name *</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Enter employee's full name"
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
              placeholder="Enter employee's email address"
              required
              disabled={loading}
            />
          </div>

          <div className="info-box">
            <h4>📧 What happens next?</h4>
            <ul>
              <li>Employee will receive an invitation email</li>
              <li>They can register using the invitation link</li>
              <li>Once registered, they'll have access to policies</li>
              <li>You can activate/deactivate them anytime</li>
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
              {loading ? 'Adding Employee...' : success ? 'Employee Added!' : 'Add Employee'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddEmployeeModal;
