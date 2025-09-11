import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import api from '../utils/api';
import './PasswordReset.css';

function PasswordReset() {
  const [searchParams] = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [formData, setFormData] = useState({
    newPassword: '',
    confirmPassword: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const validateToken = async () => {
      const token = searchParams.get('token');
      
      if (!token) {
        setError('Reset token is missing from the URL');
        setLoading(false);
        return;
      }

      try {
        await api.get(`/auth/validate-reset-token?token=${token}`);
        setTokenValid(true);
      } catch (err) {
        setError('Invalid or expired reset token. Please request a new password reset.');
      } finally {
        setLoading(false);
      }
    };

    validateToken();
  }, [searchParams]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (formData.newPassword !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (formData.newPassword.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const token = searchParams.get('token');
      await api.post('/auth/reset-password', {
        token: token,
        newPassword: formData.newPassword,
        confirmPassword: formData.confirmPassword
      });

      setMessage('Password reset successful! You can now log in with your new password.');
      
      // Redirect to login after 3 seconds
      setTimeout(() => {
        navigate('/login');
      }, 3000);

    } catch (err) {
      setError(err.response?.data?.error || 'Failed to reset password. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="password-reset-container">
        <div className="password-reset-card">
          <div className="loading-spinner"></div>
          <h2>Validating Reset Token</h2>
          <p>Please wait while we validate your password reset request...</p>
        </div>
      </div>
    );
  }

  if (!tokenValid) {
    return (
      <div className="password-reset-container">
        <div className="password-reset-card">
          <div className="error-icon">❌</div>
          <h2>Invalid Reset Link</h2>
          <p className="error-message">{error}</p>
          <div className="action-buttons">
            <Link to="/forgot-password" className="btn-primary">Request New Reset</Link>
            <Link to="/login" className="btn-secondary">Back to Login</Link>
          </div>
        </div>
      </div>
    );
  }

  if (message) {
    return (
      <div className="password-reset-container">
        <div className="password-reset-card">
          <div className="success-icon">✅</div>
          <h2>Password Reset Successful!</h2>
          <p className="success-message">{message}</p>
          <p className="redirect-message">Redirecting to login page in 3 seconds...</p>
          <div className="action-buttons">
            <Link to="/login" className="btn-primary">Login Now</Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="password-reset-container">
      <div className="password-reset-card">
        <h2>Reset Your Password</h2>
        <p>Please enter your new password below</p>

        <form onSubmit={handleSubmit} className="password-reset-form">
          <div className="form-group">
            <label htmlFor="newPassword">New Password</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              placeholder="Enter new password"
              minLength="6"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm New Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Confirm new password"
              minLength="6"
              required
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" disabled={submitting} className="submit-btn">
            {submitting ? 'Resetting Password...' : 'Reset Password'}
          </button>
        </form>

        <div className="back-to-login">
          <Link to="/login">Back to Login</Link>
        </div>
      </div>
    </div>
  );
}

export default PasswordReset;
