import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import Transactions from './components/Transactions';
import AdminDashboard from './components/AdminDashboard';
import AuthService from './services/AuthService';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(AuthService.isAuthenticated());
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user && user.roles && user.roles.includes('ROLE_ADMIN')) {
            setIsAdmin(true);
        } else {
            setIsAdmin(false);
        }
    }, [isAuthenticated]);

    const handleLogin = () => {
        setIsAuthenticated(true);
    };

    const handleLogout = () => {
        AuthService.logout();
        setIsAuthenticated(false);
        setIsAdmin(false);
    };

    return (
        <Router>
            <div className="App">
                <Navbar
                    isAuthenticated={isAuthenticated}
                    isAdmin={isAdmin}
                    onLogout={handleLogout}
                />

                <div className="container mt-4">
                    <Routes>
                        <Route path="/login" element={<Login onLogin={handleLogin} />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/dashboard" element={isAuthenticated && !isAdmin ? <Dashboard /> : <Navigate to={isAdmin ? "/admin-dashboard" : "/login"} />} />
                        <Route path="/admin-dashboard" element={isAuthenticated && isAdmin ? <AdminDashboard /> : <Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
                        <Route path="/transactions" element={isAuthenticated ? <Transactions /> : <Navigate to="/login" />} />
                        <Route path="/" element={<Navigate to={isAuthenticated ? (isAdmin ? "/admin-dashboard" : "/dashboard") : "/login"} />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
