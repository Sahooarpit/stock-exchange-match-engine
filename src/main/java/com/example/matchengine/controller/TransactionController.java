package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.Trade;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TradeRepository tradeRepository;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<List<Trade>> getTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));
        List<Trade> trades = tradeRepository.findByBuyClientOrSellClient(client, client);
        return ResponseEntity.ok(trades);
    }
}
