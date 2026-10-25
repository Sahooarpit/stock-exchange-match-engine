import React, { useState, useEffect } from 'react';
import PortfolioService from '../services/PortfolioService';
import TradeService from '../services/TradeService';

const Dashboard = () => {
    const [portfolio, setPortfolio] = useState([]);
    const [usdtBalance, setUsdtBalance] = useState(0);
    const [ticker, setTicker] = useState('');
    const [quantity, setQuantity] = useState('');
    const [price, setPrice] = useState('');
    const [side, setSide] = useState('BUY');
    const [message, setMessage] = useState('');

    const fetchPortfolio = () => {
        PortfolioService.getPortfolio().then((response) => {
            const usdt = response.data.find(item => item.ticker === 'USDT');
            setUsdtBalance(usdt ? usdt.quantity : 0);
            setPortfolio(response.data.filter(item => item.ticker !== 'USDT'));
        }).catch(error => {
            console.error("Error fetching portfolio", error);
        });
    };

    useEffect(() => {
        fetchPortfolio();
    }, []);

    const handleTrade = (e) => {
        e.preventDefault();
        setMessage('');
        
        // Generate a cryptographically secure UUID to make this submission idempotent
        const orderId = window.crypto.randomUUID();
        
        TradeService.submitOrder(orderId, ticker, side, quantity, price).then((response) => {
            // Check if it was a duplicate retry that was accepted idempotently
            if (response.data && response.data.includes("Idempotent")) {
                setMessage('Trade already processed (Idempotent success)!');
            } else {
                setMessage('Trade submitted successfully!');
            }
            fetchPortfolio(); // Refresh portfolio after trade
            setTicker('');
            setQuantity('');
            setPrice('');
        }, (error) => {
            const resMessage = (error.response && error.response.data && error.response.data.message) || error.message || error.toString();
            setMessage(resMessage);
        });
    };

    return (
        <div className="row">
            <div className="col-md-7">
                <div className="card">
                    <div className="card-body">
                        <h4 className="card-title">Portfolio</h4>
                        <h5 className="card-subtitle mb-2 text-muted">USDT Balance: ${usdtBalance.toFixed(2)}</h5>
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    <th>Ticker</th>
                                    <th>Quantity</th>
                                </tr>
                            </thead>
                            <tbody>
                                {portfolio.map((item) => (
                                    <tr key={item.ticker}>
                                        <td>{item.ticker}</td>
                                        <td>{item.quantity}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div className="col-md-5">
                <div className="card">
                    <div className="card-body">
                        <h4 className="card-title">Execute Trade</h4>
                        <form onSubmit={handleTrade}>
                            <div className="mb-3">
                                <label htmlFor="ticker" className="form-label">Ticker</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="ticker"
                                    value={ticker}
                                    onChange={(e) => setTicker(e.target.value.toUpperCase())}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="quantity" className="form-label">Quantity</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="quantity"
                                    value={quantity}
                                    onChange={(e) => setQuantity(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="price" className="form-label">Price</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    id="price"
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3">
                                <label htmlFor="side" className="form-label">Side</label>
                                <select id="side" className="form-select" value={side} onChange={(e) => setSide(e.target.value)}>
                                    <option value="BUY">Buy</option>
                                    <option value="SELL">Sell</option>
                                </select>
                            </div>
                            <div className="d-grid">
                                <button type="submit" className="btn btn-primary">{side}</button>
                            </div>
                            {message && <div className="alert alert-info mt-3">{message}</div>}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;