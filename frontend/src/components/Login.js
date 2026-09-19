import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

const Login = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleLogin = (e) => {
        e.preventDefault();
        setMessage('');
        AuthService.login(username, password).then(
            (data) => {
                onLogin();
                if (data.roles && data.roles.includes('ROLE_ADMIN')) {
                    navigate('/admin-dashboard');
                } else {
                    navigate('/dashboard');
                }
            },
            (error) => {
                const resMessage =
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString();
                setMessage(resMessage);
            }
        );
    };

    return (
        <div className="row justify-content-center">
            <div className="col-md-6">
                <div className="card">
                    <div className="card-body">
                        <h3 className="card-title text-center">Login</h3>
                        <form onSubmit={handleLogin}>
                            <div className="mb-3">
                                <label htmlFor="username">Username</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="password">Password</label>
                                <input
                                    type="password"
                                    className="form-control"
                                    name="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="d-grid">
                                <button type="submit" className="btn btn-primary">Login</button>
                            </div>
                            {message && <div className="alert alert-danger mt-3">{message}</div>}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
