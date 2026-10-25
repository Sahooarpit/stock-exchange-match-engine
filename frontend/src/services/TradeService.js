import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'http://localhost:8080/trade';

class TradeService {
    submitOrder(orderId, ticker, side, quantity, price) {
        const user = AuthService.getCurrentUser();
        return axios.post(API_URL, {
            orderId,
            ticker,
            side,
            quantity: Number(quantity),
            price: Number(price)
        }, {
            headers: {
                Authorization: 'Bearer ' + user.token
            }
        });
    }
}

export default new TradeService();
