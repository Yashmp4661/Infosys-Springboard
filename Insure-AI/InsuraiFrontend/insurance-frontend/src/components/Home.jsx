import React, { useEffect, useState } from 'react';
import api from '../utils/api';
import PolicyModal from './PolicyModal';
import './Home.css';

function Home() {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPolicy, setSelectedPolicy] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    const fetchPolicies = async () => {
      try {
        const response = await api.get('/public/policies');
        console.log('Policies data:', response.data);
        setPolicies(response.data);
      } catch (err) {
        console.error('Error fetching policies:', err);
        setError('Failed to load policies. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchPolicies();
  }, []);

  const handleLearnMore = async (policyId) => {
    try {
      // Call your detailed policy endpoint
      const response = await api.get(`/public/policies/${policyId}`);
      setSelectedPolicy(response.data);
      setIsModalOpen(true);
    } catch (err) {
      console.error('Error fetching policy details:', err);
      // Fallback to basic policy data from list
      const policy = policies.find(p => p.id === policyId);
      if (policy) {
        setSelectedPolicy(policy);
        setIsModalOpen(true);
      } else {
        alert('Failed to load policy details');
      }
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedPolicy(null);
  };

  if (loading) {
    return <div className="loading">Loading policies...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="home-container">
      {/* Hero Section */}
      <section className="hero-section">
        <h1>🏥 Corporate Insurance Portal</h1>
        <p>Comprehensive insurance solutions for your business needs</p>
      </section>

      {/* Policies Section */}
      <section className="policies-section">
        <h2>Available Insurance Policies</h2>
        
        {policies.length > 0 ? (
          <div className="policies-grid">
            {policies.map((policy) => (
              <div key={policy.id} className="policy-card">
                {/* Using correct field names from PolicyDisplayResponse */}
                <h3>{policy.name || 'Policy Name Not Available'}</h3>
                <p className="policy-description">
                  {policy.description || 'No description available'}
                </p>
                <div className="policy-info">
                  <span className="coverage">
                    Coverage: ${policy.coverageAmount?.toLocaleString() || '0'}
                  </span>
                  <span className="premium">
                    Premium: ${policy.premiumPerEmployee?.toLocaleString() || '0'}/employee
                  </span>
                </div>
                {policy.keyBenefits && (
                  <p className="key-benefits">
                    <strong>Key Benefits:</strong> {policy.keyBenefits}
                  </p>
                )}
                <div className="policy-duration">
                  <span>Duration: {policy.durationMonths || 12} months</span>
                </div>
                <button 
                  className="learn-more-btn"
                  onClick={() => handleLearnMore(policy.id)}
                >
                  Learn More
                </button>
              </div>
            ))}
          </div>
        ) : (
          <div className="no-policies">
            <p>No policies available at the moment.</p>
          </div>
        )}
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <h2>Ready to Get Started?</h2>
        <p>Join thousands of businesses that trust us with their insurance needs.</p>
        <div className="cta-buttons">
          <a href="/register" className="cta-btn primary">Get Started</a>
          <a href="/login" className="cta-btn secondary">Login</a>
        </div>
      </section>

      {/* Policy Details Modal */}
      <PolicyModal 
        policy={selectedPolicy}
        isOpen={isModalOpen}
        onClose={closeModal}
      />
    </div>
  );
}

export default Home;
