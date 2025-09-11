import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getUserData, isAuthenticated, logout } from '../utils/auth';
import './Navbar.css';

function Navbar() {
  const navigate = useNavigate();
  const authenticated = isAuthenticated();
  const user = getUserData();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link className="nav-logo" to="/">
          🏥 Insurance Portal
        </Link>

        <div className="nav-links">
          <Link className="nav-link" to="/">Home</Link>
          
          {authenticated ? (
            <>
              <Link className="nav-link" to="/dashboard">Dashboard</Link>
              <span className="user-greeting">Welcome, {user?.fullName}</span>
              <button className="btn-logout" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link className="nav-link" to="/login">Login</Link>
              <Link className="nav-link" to="/register">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
