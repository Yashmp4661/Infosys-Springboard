// import React, { useState, useEffect } from 'react';
// import api from '../utils/api';
// import PolicyModal from './PolicyModal';
// import AddPolicyModal from './modals/AddPolicyModal';
// import EditPolicyModal from './modals/EditPolicyModal';
// import './PolicyManagement.css';

// function PolicyManagement({ onBack }) {
//   const [policies, setPolicies] = useState([]);
//   const [inactivePolicies, setInactivePolicies] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState('');
//   const [selectedPolicy, setSelectedPolicy] = useState(null);
//   const [showPolicyModal, setShowPolicyModal] = useState(false);
//   const [showAddModal, setShowAddModal] = useState(false);
//   const [showEditModal, setShowEditModal] = useState(false);
//   const [editingPolicy, setEditingPolicy] = useState(null);
//   const [activeTab, setActiveTab] = useState('active');
//   const [stats, setStats] = useState(null);
//   const [activeView, setActiveView] = useState('all-policies');

//   useEffect(() => {
//     loadData();
//   }, []);

//   const loadData = async () => {
//     setLoading(true);
//     try {
//       await Promise.all([
//         loadActivePolicies(),
//         loadInactivePolicies(),
//         loadDashboardStats()
//       ]);
//     } catch (err) {
//       console.error('Error loading policy data:', err);
//       setError('Failed to load policy data');
//     } finally {
//       setLoading(false);
//     }
//   };

//   const loadActivePolicies = async () => {
//     try {
//       const response = await api.get('/provider-admin/policies');
//       setPolicies(response.data || []);
//     } catch (err) {
//       console.error('Error loading active policies:', err);
//       setPolicies([]);
//     }
//   };

//   const loadInactivePolicies = async () => {
//     try {
//       const response = await api.get('/provider-admin/policies/inactive');
//       setInactivePolicies(response.data || []);
//     } catch (err) {
//       console.error('Error loading inactive policies:', err);
//       setInactivePolicies([]);
//     }
//   };

//   const loadDashboardStats = async () => {
//     try {
//       const response = await api.get('/provider-admin/dashboard/stats');
//       setStats(response.data);
//     } catch (err) {
//       console.error('Error loading dashboard stats:', err);
//     }
//   };

//   const handlePolicyClick = (policy) => {
//     setSelectedPolicy(policy);
//     setShowPolicyModal(true);
//   };

//   const handleEditPolicy = (policy) => {
//     setEditingPolicy(policy);
//     setShowEditModal(true);
//   };

//   const handleDeletePolicy = async (policyId, policyName) => {
//     if (!window.confirm(`Are you sure you want to deactivate "${policyName}"? This action can be reversed later.`)) {
//       return;
//     }

//     try {
//       await api.delete(`/provider-admin/policies/${policyId}`);
//       await loadData();
//       alert('Policy deactivated successfully!');
//     } catch (err) {
//       console.error('Error deactivating policy:', err);
//       alert('Failed to deactivate policy. Please try again.');
//     }
//   };

//   const handleRestorePolicy = async (policyId, policyName) => {
//     if (!window.confirm(`Are you sure you want to restore "${policyName}"?`)) {
//       return;
//     }

//     try {
//       await api.put(`/provider-admin/policies/${policyId}/restore`);
//       await loadData();
//       alert('Policy restored successfully!');
//     } catch (err) {
//       console.error('Error restoring policy:', err);
//       alert('Failed to restore policy. Please try again.');
//     }
//   };

//   const onPolicyAdded = () => {
//     setShowAddModal(false);
//     loadData();
//   };

//   const onPolicyUpdated = () => {
//     setShowEditModal(false);
//     setEditingPolicy(null);
//     loadData();
//   };

//   const renderPolicyTable = (policyList, showActions = true, isActive = true) => (
//     <div className="policies-table-container">
//       <table className="policies-table">
//         <thead>
//           <tr>
//             <th>ID</th>
//             <th>Policy Name</th>
//             <th>Coverage Amount</th>
//             <th>Premium/Employee</th>
//             <th>Duration</th>
//             <th>Created Date</th>
//             {showActions && <th>Actions</th>}
//           </tr>
//         </thead>
//         <tbody>
//           {policyList.map(policy => (
//             <tr key={policy.id} className="policy-row">
//               <td>{policy.id}</td>
//               <td>
//                 <div className="policy-name-cell">
//                   <strong 
//                     className="policy-name-link"
//                     onClick={() => handlePolicyClick(policy)}
//                   >
//                     {policy.name}
//                   </strong>
//                   <small className="policy-description">
//                     {policy.description?.substring(0, 50)}...
//                   </small>
//                 </div>
//               </td>
//               <td className="amount">
//                 ₹{policy.coverageAmount?.toLocaleString() || '0'}
//               </td>
//               <td className="amount">
//                 ₹{policy.premiumPerEmployee?.toLocaleString() || '0'}
//               </td>
//               <td>{policy.policyDurationMonths || 12} months</td>
//               <td>
//                 {policy.createdAt ? new Date(policy.createdAt).toLocaleDateString() : 'N/A'}
//               </td>
//               {showActions && (
//                 <td>
//                   <div className="action-buttons">
//                     <button 
//                       className="btn btn-sm btn-info"
//                       onClick={() => handlePolicyClick(policy)}
//                     >
//                       View
//                     </button>
//                     {isActive && (
//                       <>
//                         <button 
//                           className="btn btn-sm btn-warning"
//                           onClick={() => handleEditPolicy(policy)}
//                         >
//                           Edit
//                         </button>
//                         <button 
//                           className="btn btn-sm btn-danger"
//                           onClick={() => handleDeletePolicy(policy.id, policy.name)}
//                         >
//                           Deactivate
//                         </button>
//                       </>
//                     )}
//                     {!isActive && (
//                       <button 
//                         className="btn btn-sm btn-success"
//                         onClick={() => handleRestorePolicy(policy.id, policy.name)}
//                       >
//                         Restore
//                       </button>
//                     )}
//                   </div>
//                 </td>
//               )}
//             </tr>
//           ))}
//         </tbody>
//       </table>

//       {policyList.length === 0 && (
//         <div className="empty-state">
//           <h3>No Policies Found</h3>
//           <p>
//             {isActive 
//               ? "You haven't created any active policies yet. Start by adding your first policy!"
//               : "No inactive policies to display."}
//           </p>
//           {isActive && (
//             <button 
//               className="btn btn-primary"
//               onClick={() => setShowAddModal(true)}
//             >
//               Create Your First Policy
//             </button>
//           )}
//         </div>
//       )}
//     </div>
//   );

//   if (loading) {
//     return (
//       <div className="policy-management-loading">
//         <div className="spinner"></div>
//         <p>Loading policy management...</p>
//       </div>
//     );
//   }

//   return (
//     <div className="policy-management">
//       {/* Header */}
//       <div className="policy-management-header">
//         <div className="header-left">
//           <button className="back-btn" onClick={onBack}>
//             ← Back to Dashboard
//           </button>
//           <h1>Policy Management</h1>
//         </div>
//         <div className="header-right">
//           <button 
//             className="btn btn-primary"
//             onClick={() => setShowAddModal(true)}
//           >
//             <span>+</span> Add New Policy
//           </button>
//         </div>
//       </div>

//       {/* Statistics */}
//       {stats && (
//         <div className="policy-stats">
//           <div className="stat-card">
//             <h3>My Policies</h3>
//             <p className="stat-number">{stats.myPoliciesCount || 0}</p>
//           </div>
//           <div className="stat-card">
//             <h3>Total System Policies</h3>
//             <p className="stat-number">{stats.totalPoliciesCount || 0}</p>
//           </div>
//           <div className="stat-card">
//             <h3>Active Policies</h3>
//             <p className="stat-number">{policies.length}</p>
//           </div>
//           <div className="stat-card">
//             <h3>Inactive Policies</h3>
//             <p className="stat-number">{inactivePolicies.length}</p>
//           </div>
//         </div>
//       )}

//       {error && <div className="error-message">{error}</div>}

//       {/* Tabs */}
//       <div className="policy-tabs">
//         <button 
//           className={`tab-btn ${activeTab === 'active' ? 'active' : ''}`}
//           onClick={() => setActiveTab('active')}
//         >
//           Active Policies ({policies.length})
//         </button>
//         <button 
//           className={`tab-btn ${activeTab === 'inactive' ? 'active' : ''}`}
//           onClick={() => setActiveTab('inactive')}
//         >
//           Inactive Policies ({inactivePolicies.length})
//         </button>
//       </div>

//       {/* Policy Tables */}
//       <div className="policy-content">
//         {activeTab === 'active' && renderPolicyTable(policies, true, true)}
//         {activeTab === 'inactive' && renderPolicyTable(inactivePolicies, true, false)}
//       </div>

//       {/* Modals */}
//       {showPolicyModal && selectedPolicy && (
//         <PolicyModal
//           policy={selectedPolicy}
//           isOpen={showPolicyModal}
//           onClose={() => {
//             setShowPolicyModal(false);
//             setSelectedPolicy(null);
//           }}
//         />
//       )}

//       {showAddModal && (
//         <AddPolicyModal
//           onClose={() => setShowAddModal(false)}
//           onPolicyAdded={onPolicyAdded}
//         />
//       )}

//       {showEditModal && editingPolicy && (
//         <EditPolicyModal
//           policy={editingPolicy}
//           onClose={() => {
//             setShowEditModal(false);
//             setEditingPolicy(null);
//           }}
//           onPolicyUpdated={onPolicyUpdated}
//         />
//       )}
//     </div>
//   );
// }

// export default PolicyManagement;


import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import PolicyModal from './PolicyModal';
import AddPolicyModal from './modals/AddPolicyModal';
import EditPolicyModal from './modals/EditPolicyModal';
import './PolicyManagement.css';

function PolicyManagement({ onBack }) {
  const [policies, setPolicies] = useState([]);
  const [inactivePolicies, setInactivePolicies] = useState([]);
  const [myPolicies, setMyPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedPolicy, setSelectedPolicy] = useState(null);
  const [showPolicyModal, setShowPolicyModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingPolicy, setEditingPolicy] = useState(null);
  const [activeTab, setActiveTab] = useState('active');
  const [stats, setStats] = useState(null);
  const [currentView, setCurrentView] = useState('all'); // 'all', 'my-policies'

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      await Promise.all([
        loadActivePolicies(),
        loadInactivePolicies(),
        loadMyPolicies(),
        loadDashboardStats()
      ]);
    } catch (err) {
      console.error('Error loading policy data:', err);
      setError('Failed to load policy data');
    } finally {
      setLoading(false);
    }
  };

  const loadActivePolicies = async () => {
    try {
      const response = await api.get('/provider-admin/policies');
      setPolicies(response.data || []);
    } catch (err) {
      console.error('Error loading active policies:', err);
      setPolicies([]);
    }
  };

  const loadInactivePolicies = async () => {
    try {
      const response = await api.get('/provider-admin/policies/inactive');
      setInactivePolicies(response.data || []);
    } catch (err) {
      console.error('Error loading inactive policies:', err);
      setInactivePolicies([]);
    }
  };

  // NEW: Load My Policies function
  const loadMyPolicies = async () => {
    try {
      const response = await api.get('/provider-admin/policies/my-policies');
      setMyPolicies(response.data || []);
    } catch (err) {
      console.error('Error loading my policies:', err);
      setMyPolicies([]);
    }
  };

  const loadDashboardStats = async () => {
    try {
      const response = await api.get('/provider-admin/dashboard/stats');
      setStats(response.data);
    } catch (err) {
      console.error('Error loading dashboard stats:', err);
    }
  };

  const handlePolicyClick = (policy) => {
    setSelectedPolicy(policy);
    setShowPolicyModal(true);
  };

  const handleEditPolicy = (policy) => {
    setEditingPolicy(policy);
    setShowEditModal(true);
  };

  const handleDeletePolicy = async (policyId, policyName) => {
    if (!window.confirm(`Are you sure you want to deactivate "${policyName}"? This action can be reversed later.`)) {
      return;
    }

    try {
      await api.delete(`/provider-admin/policies/${policyId}`);
      await loadData();
      alert('Policy deactivated successfully!');
    } catch (err) {
      console.error('Error deactivating policy:', err);
      alert('Failed to deactivate policy. Please try again.');
    }
  };

  const handleRestorePolicy = async (policyId, policyName) => {
    if (!window.confirm(`Are you sure you want to restore "${policyName}"?`)) {
      return;
    }

    try {
      await api.put(`/provider-admin/policies/${policyId}/restore`);
      await loadData();
      alert('Policy restored successfully!');
    } catch (err) {
      console.error('Error restoring policy:', err);
      alert('Failed to restore policy. Please try again.');
    }
  };

  const onPolicyAdded = () => {
    setShowAddModal(false);
    loadData();
  };

  const onPolicyUpdated = () => {
    setShowEditModal(false);
    setEditingPolicy(null);
    loadData();
  };

  const getCurrentPolicyListForStats = () => {
    if (currentView === 'my-policies') {
      return myPolicies;
    } else if (activeTab === 'inactive') {
      return inactivePolicies;
    } else {
      return policies;
    }
  };


  // NEW: Handle stat card clicks
  const handleStatCardClick = (statType) => {
    switch (statType) {
      case 'my-policies':
        setCurrentView('my-policies');
        setActiveTab('active');
        break;
      case 'all-policies':
        setCurrentView('all');
        setActiveTab('active');
        break;
      case 'active-policies':
        setCurrentView('all');
        setActiveTab('active');
        break;
      case 'inactive-policies':
        setCurrentView('all');
        setActiveTab('inactive');
        break;
      default:
        break;
    }
  };

  // NEW: Get current policy list based on view
  const getCurrentPolicyList = () => {
    if (currentView === 'my-policies') {
      return myPolicies;
    } else {
      return activeTab === 'active' ? policies : inactivePolicies;
    }
  };


  // Add function to handle navigation to My Policies
  const handleViewMyPolicies = () => {
    setCurrentView('my-policies');
    setActiveTab('active');
  };

  const handleViewAllPolicies = () => {
    setCurrentView('all');
    setActiveTab('active');
  };

  const renderPolicyTable = (policyList, showActions = true, isActive = true, isMyPolicies = false) => (
    <div className="policies-table-container">
      <table className="policies-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Policy Name</th>
            <th>Coverage Amount</th>
            <th>Premium/Employee</th>
            <th>Duration</th>
            <th>Created Date</th>
            {showActions && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {policyList.map(policy => (
            <tr 
              key={policy.id} 
              className="policy-row clickable-row"
              onClick={() => handlePolicyClick(policy)}
            >
              <td>{policy.id}</td>
              <td>
                <div className="policy-name-cell">
                  <strong className="policy-name-link">
                    {policy.name}
                  </strong>
                  <small className="policy-description">
                    {policy.description?.substring(0, 50)}...
                  </small>
                </div>
              </td>
              <td className="amount">
                ₹{policy.coverageAmount?.toLocaleString() || '0'}
              </td>
              <td className="amount">
                ₹{policy.premiumPerEmployee?.toLocaleString() || '0'}
              </td>
              <td>{policy.policyDurationMonths || 12} months</td>
              <td>
                {policy.createdAt ? new Date(policy.createdAt).toLocaleDateString() : 'N/A'}
              </td>
              {showActions && (
                <td onClick={(e) => e.stopPropagation()}>
                  <div className="action-buttons">
                    <button 
                      className="btn btn-sm btn-info"
                      onClick={() => handlePolicyClick(policy)}
                    >
                      View
                    </button>
                    {/* Show edit/delete only for My Policies or active policies */}
                    {(isMyPolicies || (isActive && currentView !== 'my-policies')) && (
                      <>
                        <button 
                          className="btn btn-sm btn-warning"
                          onClick={() => handleEditPolicy(policy)}
                        >
                          Edit
                        </button>
                        <button 
                          className="btn btn-sm btn-danger"
                          onClick={() => handleDeletePolicy(policy.id, policy.name)}
                        >
                          Deactivate
                        </button>
                      </>
                    )}
                    {!isActive && (
                      <button 
                        className="btn btn-sm btn-success"
                        onClick={() => handleRestorePolicy(policy.id, policy.name)}
                      >
                        Restore
                      </button>
                    )}
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>

      {policyList.length === 0 && (
        <div className="empty-state">
          <h3>No Policies Found</h3>
          <p>
            {currentView === 'my-policies' 
              ? "You haven't created any policies yet. Start by adding your first policy!"
              : isActive 
                ? "No active policies to display."
                : "No inactive policies to display."}
          </p>
          {currentView === 'my-policies' && (
            <button 
              className="btn btn-primary"
              onClick={() => setShowAddModal(true)}
            >
              Create Your First Policy
            </button>
          )}
        </div>
      )}
    </div>
  );

  if (loading) {
    return (
      <div className="policy-management-loading">
        <div className="spinner"></div>
        <p>Loading policy management...</p>
      </div>
    );
  }

  return (
    <div className="policy-management">
      {/* Header */}
      <div className="policy-management-header">
        <div className="header-left">
          <button className="back-btn" onClick={onBack}>
            ← Back to Dashboard
          </button>
          <h1>Policy Management</h1>
          <p>
            {currentView === 'my-policies' 
              ? 'Viewing policies created by you'
              : 'Viewing all system policies'}
          </p>
        </div>
        <div className="header-right">
          <button 
            className="btn btn-primary"
            onClick={() => setShowAddModal(true)}
          >
            <span>+</span> Add New Policy
          </button>
        </div>
      </div>

      {/* UPDATED: Clickable Statistics Cards */}
      {stats && (
        <div className="policy-stats">
          <button 
            className="stat-card clickable-stat-card"
            onClick={() => handleStatCardClick('my-policies')}
          >
            <h3>My Policies</h3>
            <p className="stat-number">{myPolicies.length || 0}</p>
            <small>Click to view your created policies</small>
          </button>
          
          <button 
            className="stat-card clickable-stat-card"
            onClick={() => handleStatCardClick('all-policies')}
          >
            <h3>Total System Policies</h3>
            <p className="stat-number">{stats.totalPoliciesCount || 0}</p>
            <small>Click to view all policies</small>
          </button>
          
          <button 
            className="stat-card clickable-stat-card"
            onClick={() => handleStatCardClick('active-policies')}
          >
            <h3>Active Policies</h3>
            <p className="stat-number">{policies.length}</p>
            <small>Click to view active policies</small>
          </button>
          
          <button 
            className="stat-card clickable-stat-card"
            onClick={() => handleStatCardClick('inactive-policies')}
          >
            <h3>Inactive Policies</h3>
            <p className="stat-number">{inactivePolicies.length}</p>
            <small>Click to view inactive policies</small>
          </button>
        </div>
      )}

      {error && <div className="error-message">{error}</div>}

      {/* Conditional Tab Display */}
      {currentView !== 'my-policies' && (
        <div className="policy-tabs">
          <button 
            className={`tab-btn ${activeTab === 'active' ? 'active' : ''}`}
            onClick={() => setActiveTab('active')}
          >
            Active Policies ({policies.length})
          </button>
          <button 
            className={`tab-btn ${activeTab === 'inactive' ? 'active' : ''}`}
            onClick={() => setActiveTab('inactive')}
          >
            Inactive Policies ({inactivePolicies.length})
          </button>
        </div>
      )}

      {/* Policy Table */}
      <div className="policy-content">
        {currentView === 'my-policies' ? (
          <div className="my-policies-section">
            <div className="section-header">
              <h2>My Created Policies ({myPolicies.length})</h2>
              <button 
                className="btn btn-outline"
                onClick={() => setCurrentView('all')}
              >
                View All Policies →
              </button>
            </div>
            {renderPolicyTable(myPolicies, true, true, true)}
          </div>
        ) : (
          <>
            {activeTab === 'active' && renderPolicyTable(policies, true, true, false)}
            {activeTab === 'inactive' && renderPolicyTable(inactivePolicies, true, false, false)}
          </>
        )}
      </div>

        <div className="policy-stats">
        <div className="stat-card">
          <h3>
            {currentView === 'my-policies' ? 'My Policies' : 'Total Policies'}
          </h3>
          <p className="stat-number">{getCurrentPolicyListForStats().length}</p>
        </div>
        <div className="stat-card">
          <h3>Total Coverage</h3>
          <p className="stat-number">
            ₹{getCurrentPolicyListForStats().reduce((sum, p) => sum + (p.coverageAmount || 0), 0).toLocaleString()}
          </p>
        </div>
        <div className="stat-card">
          <h3>Avg Premium</h3>
          <p className="stat-number">
            ₹{getCurrentPolicyListForStats().length > 0 
              ? Math.round(getCurrentPolicyListForStats().reduce((sum, p) => sum + (p.premiumPerEmployee || 0), 0) / getCurrentPolicyListForStats().length).toLocaleString()
              : '0'}
          </p>
        </div>
      </div>

      {/* Modals */}
      {showPolicyModal && selectedPolicy && (
        <PolicyModal
          policy={selectedPolicy}
          isOpen={showPolicyModal}
          onClose={() => {
            setShowPolicyModal(false);
            setSelectedPolicy(null);
          }}
        />
      )}

      {showAddModal && (
        <AddPolicyModal
          onClose={() => setShowAddModal(false)}
          onPolicyAdded={onPolicyAdded}
        />
      )}

      {showEditModal && editingPolicy && (
        <EditPolicyModal
          policy={editingPolicy}
          onClose={() => {
            setShowEditModal(false);
            setEditingPolicy(null);
          }}
          onPolicyUpdated={onPolicyUpdated}
        />
      )}
    </div>
  );
}

export default PolicyManagement;
