package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "ticker", length = 10, nullable = false)
    private String ticker; // Changed from Ticker enum to String

    @Enumerated(EnumType.STRING)
    private Side side;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    private long originalQuantity;
    private long remainingQuantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    public Order(Client client, String ticker, Side side, long quantity, BigDecimal price, String orderId) {
        this.orderId = (orderId != null && !orderId.trim().isEmpty()) ? orderId : UUID.randomUUID().toString();
        this.client = client;
        this.ticker = ticker;
        this.side = side;
        this.price = price;
        this.originalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.OPEN;
        this.createdAt = Instant.now();
    }
}