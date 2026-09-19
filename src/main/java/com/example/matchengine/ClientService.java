package com.example.matchengine;

import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.InstrumentRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final InstrumentRepository instrumentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Client registerClient(Client client) {
        if (clientRepository.findByUsername(client.getUsername()).isPresent()) {
            throw new IllegalStateException("Username is already taken!");
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    @Transactional
    public void lockFundsAndValidateOrder(Order order) {
        Client client = order.getClient();

        instrumentRepository.findById(order.getTicker())
                .orElseThrow(() -> new IllegalStateException("Invalid ticker: " + order.getTicker()));

        if (order.getSide() == Side.BUY) {
            BigDecimal requiredCash = order.getPrice().multiply(BigDecimal.valueOf(order.getOriginalQuantity()));
            PortfolioHolding cashHolding = getOrCreateHolding(client.getClientId(), "USDT");

            if (cashHolding.getQuantity().compareTo(requiredCash) < 0) {
                throw new IllegalStateException("Insufficient funds. Required: " + requiredCash + ", Available: " + cashHolding.getQuantity());
            }

            cashHolding.setQuantity(cashHolding.getQuantity().subtract(requiredCash));
            cashHolding.setLockedQuantity(cashHolding.getLockedQuantity().add(requiredCash));
            portfolioHoldingRepository.save(cashHolding);

        } else { // SELL order
            PortfolioHolding stockHolding = getOrCreateHolding(client.getClientId(), order.getTicker());
            BigDecimal requiredStock = BigDecimal.valueOf(order.getOriginalQuantity());

            if (stockHolding.getQuantity().compareTo(requiredStock) < 0) {
                throw new IllegalStateException("Insufficient stock. Required: " + requiredStock + ", Available: " + stockHolding.getQuantity());
            }

            stockHolding.setQuantity(stockHolding.getQuantity().subtract(requiredStock));
            stockHolding.setLockedQuantity(stockHolding.getLockedQuantity().add(requiredStock));
            portfolioHoldingRepository.save(stockHolding);
        }
    }

    @Transactional
    public void executeTrade(Trade trade, Order buyOrder, Order sellOrder) {
        BigDecimal tradeQty = BigDecimal.valueOf(trade.getQuantity());
        BigDecimal tradePrice = trade.getPrice();
        BigDecimal tradeValue = tradeQty.multiply(tradePrice);

        // 1. Process Buyer
        PortfolioHolding buyerUsdt = getOrCreateHolding(buyOrder.getClient().getClientId(), "USDT");
        PortfolioHolding buyerTicker = getOrCreateHolding(buyOrder.getClient().getClientId(), trade.getTicker());
        
        // Deduct from buyer's locked USDT at the original limit price they locked it at
        BigDecimal lockedValueToDeduct = tradeQty.multiply(buyOrder.getPrice());
        buyerUsdt.setLockedQuantity(buyerUsdt.getLockedQuantity().subtract(lockedValueToDeduct));
        
        // Refund the difference if the trade executed at a better (lower) price than the limit order
        BigDecimal refundAmount = lockedValueToDeduct.subtract(tradeValue);
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            buyerUsdt.setQuantity(buyerUsdt.getQuantity().add(refundAmount));
        }
        
        // Buyer gains the ticker quantity
        buyerTicker.setQuantity(buyerTicker.getQuantity().add(tradeQty));
        portfolioHoldingRepository.save(buyerUsdt);
        portfolioHoldingRepository.save(buyerTicker);

        // 2. Process Seller
        PortfolioHolding sellerTicker = getOrCreateHolding(sellOrder.getClient().getClientId(), trade.getTicker());
        PortfolioHolding sellerUsdt = getOrCreateHolding(sellOrder.getClient().getClientId(), "USDT");

        // Deduct from seller's locked Ticker
        sellerTicker.setLockedQuantity(sellerTicker.getLockedQuantity().subtract(tradeQty));

        // Seller gains the USDT trade value
        sellerUsdt.setQuantity(sellerUsdt.getQuantity().add(tradeValue));
        portfolioHoldingRepository.save(sellerTicker);
        portfolioHoldingRepository.save(sellerUsdt);
    }
    
    @Transactional
    public void unlockFunds(Order order) {
        if (order.getSide() == Side.BUY) {
            BigDecimal lockedAmount = order.getPrice().multiply(BigDecimal.valueOf(order.getRemainingQuantity()));
            PortfolioHolding cashHolding = getOrCreateHolding(order.getClient().getClientId(), "USDT");
            
            cashHolding.setLockedQuantity(cashHolding.getLockedQuantity().subtract(lockedAmount));
            cashHolding.setQuantity(cashHolding.getQuantity().add(lockedAmount));
            portfolioHoldingRepository.save(cashHolding);
        } else {
            BigDecimal lockedStock = BigDecimal.valueOf(order.getRemainingQuantity());
            PortfolioHolding stockHolding = getOrCreateHolding(order.getClient().getClientId(), order.getTicker());
            
            stockHolding.setLockedQuantity(stockHolding.getLockedQuantity().subtract(lockedStock));
            stockHolding.setQuantity(stockHolding.getQuantity().add(lockedStock));
            portfolioHoldingRepository.save(stockHolding);
        }
    }

    private PortfolioHolding getOrCreateHolding(String clientId, String ticker) {
        return portfolioHoldingRepository.findByClientClientIdAndTicker(clientId, ticker)
                .orElseGet(() -> {
                    Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalStateException("Client not found: " + clientId));
                    PortfolioHolding holding = new PortfolioHolding(client, ticker, BigDecimal.ZERO);
                    holding.setLockedQuantity(BigDecimal.ZERO);
                    return holding;
                });
    }

}