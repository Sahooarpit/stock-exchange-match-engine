import React from 'react';
import { Link } from 'react-router-dom';

function Navbar({ isAuthenticated, isAdmin, onLogout }) {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container-fluid">
                <Link className="navbar-brand" to="/">Match Engine</Link>
                <div className="collapse navbar-collapse">
                    <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                        {isAuthenticated ? (
                            <>
                                {isAdmin ? (
                                    <li className="nav-item">
                                        <Link className="nav-link" to="/admin-dashboard">Admin Dashboard</Link>
                                    </li>
                                ) : (
                                    <li className="nav-item">
                                        <Link className="nav-link" to="/dashboard">Dashboard</Link>
                                    </li>
                                )}
                                <li className="nav-item">
                                    <Link className="nav-link" to="/transactions">Transactions</Link>
                                </li>
                                <li className="nav-item">
                                    <button className="btn btn-outline-light ms-2" onClick={onLogout}>Logout</button>
                                </li>
                            </>
                        ) : (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/login">Login</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/register">Register</Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
