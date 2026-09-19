package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_holdings")
@Getter
@Setter
@NoArgsConstructor
@IdClass(PortfolioHoldingId.class)
public class PortfolioHolding {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Id
    @Column(name = "ticker", length = 10)
    private String ticker; // Changed from Ticker enum to String

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal lockedQuantity = BigDecimal.ZERO;

    public PortfolioHolding(Client client, String ticker, BigDecimal quantity) {
        this.client = client;
        this.ticker = ticker;
        this.quantity = quantity;
    }
}

