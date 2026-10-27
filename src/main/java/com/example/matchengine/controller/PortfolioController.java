package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.ClientService;
import com.example.matchengine.Order;
import com.example.matchengine.PortfolioHolding;
import com.example.matchengine.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<PortfolioHolding>> getPortfolio() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));
        List<PortfolioHolding> holdings = portfolioService.getPortfolio(client.getClientId());
        return ResponseEntity.ok(holdings);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getPendingOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));
        List<Order> pendingOrders = portfolioService.getPendingOrders(client.getClientId());
        return ResponseEntity.ok(pendingOrders);
    }
}
