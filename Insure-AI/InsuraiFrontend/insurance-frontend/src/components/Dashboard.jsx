
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/api';
import { getUserData, isAuthenticated, logout } from '../utils/auth';
import AddEmployeeModal from './modals/AddEmployeeModal';
import AddAdminModal from './modals/AddAdminModal';
import PolicyManagement from './PolicyManagement';
import './Dashboard.css';

function Dashboard() {
  const [user, setUser] = useState(null);
  const [policies, setPolicies] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('overview');
  const [showAddEmployeeModal, setShowAddEmployeeModal] = useState(false);
  const [showAddAdminModal, setShowAddAdminModal] = useState(false);
  const [showPolicyManagement, setShowPolicyManagement] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate('/login');
      return;
    }

    const userData = getUserData();
    setUser(userData);
    loadDashboardData(userData.role);
  }, [navigate]);

  const onAdminAdded = () => {
  setShowAddAdminModal(false);
  // Optional: You can load admin list here if you have it
  console.log('New administrator created successfully!');
};

  const loadDashboardData = async (role) => {
    try {
      // Load policies for all users
      const policiesRes = await api.get('/public/policies');
      setPolicies(policiesRes.data);

      // Load role-specific data
      if (role === 'CORPORATE_ADMIN') {
        await loadEmployees();
      }
    } catch (err) {
      console.error('Dashboard data error:', err);
      setError('Failed to load dashboard data');
      // Set some sample data for testing
      setPolicies([
        {
          id: 1,
          policyName: "Health Insurance",
          policyDetails: "Basic health coverage",
          coverageAmount: 50000,
          premiumAmount: 300
        }
      ]);
    } finally {
      setLoading(false);
    }
  };
const loadEmployees = async () => {
  console.log('🔍 Loading employees for Corporate Admin...');
  
  try {
    // Using your exact endpoint
    console.log('📡 Making request to /api/corporate/employees');
    const employeesRes = await api.get('/corporate/employees');
    
    console.log('✅ API Response received:', employeesRes);
    console.log('📋 Employee data:', employeesRes.data);
    console.log('📊 Number of employees:', employeesRes.data?.length);
    
    setEmployees(employeesRes.data || []);
  } catch (err) {
    console.error('❌ Error loading employees:', err);
    console.error('❌ Error response:', err.response?.data);
    console.error('❌ Error status:', err.response?.status);
    
    setEmployees([]);
  }
};


  const handleEmployeeAdded = () => {
    setShowAddEmployeeModal(false);
    loadEmployees(); // Refresh employee list
  };

  const handleToggleEmployeeStatus = async (employeeId, currentStatus) => {
    try {
      // Using your exact endpoint and request format
      await api.put(`/corporate/employees/${employeeId}/status`, {
        active: !currentStatus
      });
      
      // Refresh employee list
      await loadEmployees();
      console.log('Employee status updated successfully');
    } catch (error) {
      console.error('Error updating employee status:', error);
      alert('Failed to update employee status. Please try again.');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return <div className="dashboard-loading">Loading dashboard...</div>;
  }

  if (!user) {
    return <div className="dashboard-error">Please log in to access dashboard</div>;
  }



const renderAdminDashboard = () => {
  if (showPolicyManagement) {
    return (
      <PolicyManagement 
        onBack={() => setShowPolicyManagement(false)}
      />
    );
  }

  return (
    <div className="admin-dashboard">
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Policies</h3>
          <p className="stat-number">{policies.length}</p>
        </div>
        <div className="stat-card">
          <h3>Active Users</h3>
          <p className="stat-number">1,234</p>
        </div>
        <div className="stat-card">
          <h3>Claims Processed</h3>
          <p className="stat-number">856</p>
        </div>
        <div className="stat-card">
          <h3>Revenue</h3>
          <p className="stat-number">$2.4M</p>
        </div>
      </div>

      <div className="dashboard-section">
        <h3>System Management</h3>
        <div className="action-buttons">
          <button 
            className="action-btn primary"
            onClick={() => setShowAddAdminModal(true)}
          >
            <span>👨‍💼</span> Add Administrator
          </button>
          <button 
            className="action-btn primary"
            onClick={() => setShowPolicyManagement(true)}
          >
            <span>📋</span> Manage Policies
          </button>
          <button className="action-btn secondary">
            <span>📊</span> View Reports
          </button>
          <button className="action-btn secondary">
            <span>👥</span> User Management
          </button>
        </div>
      </div>
    </div>
  );
};


  const renderCorporateDashboard = () => (
    <div className="corporate-dashboard">
      {/* Tab Navigation for Corporate Admin */}
      <div className="tab-navigation">
        <button 
          className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          📊 Overview
        </button>
        <button 
          className={`tab-btn ${activeTab === 'employees' ? 'active' : ''}`}
          onClick={() => setActiveTab('employees')}
        >
          👥 Employees ({employees.length})
        </button>
        <button 
          className={`tab-btn ${activeTab === 'policies' ? 'active' : ''}`}
          onClick={() => setActiveTab('policies')}
        >
          📋 Policies
        </button>
      </div>

      {/* Tab Content */}
      <div className="tab-content">
        {activeTab === 'overview' && (
          <div className="overview-tab">
            <div className="stats-grid">
              <div className="stat-card">
                <h3>My Employees</h3>
                <p className="stat-number">{employees.length}</p>
              </div>
              <div className="stat-card">
                <h3>Active Policies</h3>
                <p className="stat-number">{policies.length}</p>
              </div>
              <div className="stat-card">
                <h3>Pending Claims</h3>
                <p className="stat-number">12</p>
              </div>
              <div className="stat-card">
                <h3>Monthly Premium</h3>
                <p className="stat-number">$15,200</p>
              </div>
            </div>

            <div className="dashboard-section">
              <h3>Quick Actions</h3>
              <div className="action-buttons">
                <button 
                  className="action-btn primary"
                  onClick={() => setShowAddEmployeeModal(true)}
                >
                  👥 Add Employee
                </button>
                <button 
                  className="action-btn secondary"
                  onClick={() => setActiveTab('employees')}
                >
                  📋 Manage Employees
                </button>
                <button className="action-btn secondary">📄 View Reports</button>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'employees' && (
          <div className="employees-tab">
            <div className="tab-header">
              <h2>Employee Management</h2>
              <button 
                className="btn btn-primary"
                onClick={() => setShowAddEmployeeModal(true)}
              >
                <span>+</span> Add New Employee
              </button>
            </div>
            
            <div className="employees-table-container">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Status</th>
                    <th>Registration Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map(employee => (
                    <tr key={employee.id}>
                      <td>{employee.id}</td>
                      <td>{employee.fullName}</td>
                      <td>{employee.email}</td>
                      <td>
                        <span className={`status-badge ${employee.isActive ? 'active' : 'inactive'}`}>
                          {employee.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td>
                        <span className={`status-badge ${employee.isEnabled ? 'registered' : 'pending'}`}>
                          {employee.isEnabled ? 'Registered' : 'Pending Registration'}
                        </span>
                      </td>
                      <td>
                        <div className="action-buttons">
                          <button 
                            className={`btn btn-sm ${employee.isActive ? 'btn-warning' : 'btn-success'}`}
                            onClick={() => handleToggleEmployeeStatus(employee.id, employee.isActive)}
                          >
                            {employee.isActive ? 'Deactivate' : 'Activate'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              
              {employees.length === 0 && (
                <div className="empty-state">
                  <h3>No Employees Added Yet</h3>
                  <p>Start by adding your first employee to manage your team's insurance coverage.</p>
                  <button 
                    className="btn btn-primary"
                    onClick={() => setShowAddEmployeeModal(true)}
                  >
                    Add Your First Employee
                  </button>
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'policies' && (
          <div className="policies-tab">
            <h2>Available Insurance Policies</h2>
            <div className="policies-grid">
              {policies.map((policy) => (
                <div key={policy.id} className="policy-card">
                  <h4>{policy.policyName}</h4>
                  <p>{policy.policyDetails}</p>
                  <div className="policy-footer">
                    <span className="coverage">Coverage: ${policy.coverageAmount?.toLocaleString()}</span>
                    <span className="premium">Premium: ${policy.premiumAmount?.toLocaleString()}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );

  const renderEmployeeDashboard = () => (
    <div className="employee-dashboard">
      <div className="stats-grid">
        <div className="stat-card">
          <h3>My Policies</h3>
          <p className="stat-number">3</p>
        </div>
        <div className="stat-card">
          <h3>Active Claims</h3>
          <p className="stat-number">1</p>
        </div>
        <div className="stat-card">
          <h3>Coverage Amount</h3>
          <p className="stat-number">$50,000</p>
        </div>
        <div className="stat-card">
          <h3>Next Premium</h3>
          <p className="stat-number">Dec 15</p>
        </div>
      </div>

      <div className="dashboard-section">
        <h3>Quick Actions</h3>
        <div className="action-buttons">
          <button className="action-btn primary">Submit Claim</button>
          <button className="action-btn secondary">View Policies</button>
          <button className="action-btn secondary">Download Documents</button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div className="header-left">
          <h1>Welcome back, {user.fullName}!</h1>
          <p className="user-role">{user.role.replace('_', ' ')}</p>
        </div>
        {/* <div className="header-right">
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div> */}
      </div>

      {error && <div className="dashboard-error">{error}</div>}

      <div className="dashboard-content">
        {user.role === 'PROVIDER_ADMIN' && renderAdminDashboard()}
        {user.role === 'CORPORATE_ADMIN' && renderCorporateDashboard()}
        {user.role === 'EMPLOYEE' && renderEmployeeDashboard()}
      </div>

      {/* Add Employee Modal */}
      {showAddEmployeeModal && (
        <AddEmployeeModal
          onClose={() => setShowAddEmployeeModal(false)}
          onEmployeeAdded={handleEmployeeAdded}
        />
      )}
        {/* Add Admin Modal */}
       {showAddAdminModal && (
      <AddAdminModal
        onClose={() => setShowAddAdminModal(false)}
        onAdminAdded={onAdminAdded}
      />
    )}
    </div>
  );
}

export default Dashboard;

