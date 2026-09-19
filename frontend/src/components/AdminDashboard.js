import React, { useState } from 'react';

const AdminDashboard = () => {
    const [clientUsername, setClientUsername] = useState('');
    const [assetType, setAssetType] = useState('USDT');
    const [amount, setAmount] = useState('');
    const [message, setMessage] = useState('');

    const handleAssign = (e) => {
        e.preventDefault();
        setMessage('');
        
        // This should point to a specific Admin API in the backend
        // e.g., axios.post('/api/admin/assign-funds', { clientUsername, assetType, amount })
        // Since we are mocking the UI part to start:
        
        setMessage(`Successfully assigned ${amount} ${assetType} to ${clientUsername}.`);
        setClientUsername('');
        setAmount('');
        setAssetType('USDT');
    };

    return (
        <div className="row justify-content-center">
            <div className="col-md-8">
                <div className="card">
                    <div className="card-header bg-dark text-white">
                        <h4 className="mb-0">Admin Dashboard - Manage Client Funds</h4>
                    </div>
                    <div className="card-body">
                        <form onSubmit={handleAssign}>
                            <div className="mb-3">
                                <label className="form-label">Client Username</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Enter client's username"
                                    value={clientUsername}
                                    onChange={(e) => setClientUsername(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="row">
                                <div className="col-md-4 mb-3">
                                    <label className="form-label">Asset Type</label>
                                    <select 
                                        className="form-select" 
                                        value={assetType}
                                        onChange={(e) => setAssetType(e.target.value)}
                                    >
                                        <option value="USDT">USDT</option>
                                        <option value="BTC">BTC</option>
                                        <option value="ETH">ETH</option>
                                        <option value="AAPL">AAPL</option>
                                        <option value="TSLA">TSLA</option>
                                    </select>
                                </div>
                                <div className="col-md-8 mb-3">
                                    <label className="form-label">Amount</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        className="form-control"
                                        placeholder="Enter amount"
                                        value={amount}
                                        onChange={(e) => setAmount(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>
                            <button type="submit" className="btn btn-primary w-100">Assign Funds</button>
                        </form>
                        
                        {message && (
                            <div className="alert alert-success mt-3">
                                {message}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
