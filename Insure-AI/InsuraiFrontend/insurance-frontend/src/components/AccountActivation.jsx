import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import api from '../utils/api';
import './AccountActivation.css';

function AccountActivation() {
  const [searchParams] = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [userInfo, setUserInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const activateAccount = async () => {
      const token = searchParams.get('token');
      
      if (!token) {
        setError('Activation token is missing from the URL');
        setLoading(false);
        return;
      }

      try {
        const response = await api.get(`/auth/activate?token=${token}`);
        setMessage(response.data.message);
        setUserInfo({
          email: response.data.email,
          fullName: response.data.fullName
        });

        // Redirect to login after 5 seconds
        setTimeout(() => {
          navigate('/login');
        }, 5000);

      } catch (err) {
        setError(err.response?.data?.error || 'Account activation failed');
      } finally {
        setLoading(false);
      }
    };

    activateAccount();
  }, [searchParams, navigate]);

  if (loading) {
    return (
      <div className="activation-container">
        <div className="activation-card">
          <div className="loading-spinner"></div>
          <h2>Activating Your Account</h2>
          <p>Please wait while we activate your account...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="activation-container">
      <div className="activation-card">
        {message ? (
          <div className="success-content">
            <div className="success-icon">✅</div>
            <h2>Account Activated Successfully!</h2>
            <p className="success-message">{message}</p>
            {userInfo && (
              <div className="user-info">
                <p><strong>Welcome, {userInfo.fullName}!</strong></p>
                <p>Email: {userInfo.email}</p>
              </div>
            )}
            <p className="redirect-message">You will be redirected to the login page in 5 seconds...</p>
            <div className="action-buttons">
              <Link to="/login" className="btn-primary">Login Now</Link>
              <Link to="/" className="btn-secondary">Go to Home</Link>
            </div>
          </div>
        ) : (
          <div className="error-content">
            <div className="error-icon">❌</div>
            <h2>Activation Failed</h2>
            <p className="error-message">{error}</p>
            <div className="action-buttons">
              <Link to="/register" className="btn-primary">Register Again</Link>
              <Link to="/" className="btn-secondary">Go to Home</Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default AccountActivation;
