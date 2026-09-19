package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.ClientService;
import com.example.matchengine.MatchEngine;
import com.example.matchengine.Order;
import com.example.matchengine.Trade;
import com.example.matchengine.dto.OrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TradeController {

    private final MatchEngine matchEngine;
    private final ClientService clientService;

    @PostMapping("/trade")
    public ResponseEntity<String> submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));

        Order order = new Order(
                client,
                orderRequest.getTicker(),
                orderRequest.getSide(),
                orderRequest.getQuantity(),
                orderRequest.getPrice()
        );

        List<Trade> trades = matchEngine.processOrder(order);
        String responseMessage = "Order processed. " + trades.size() + " trade(s) executed.";
        return ResponseEntity.ok(responseMessage);
    }
    
    @PostMapping("/trade/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam String orderId) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        matchEngine.cancelOrder(orderId, username);
        return ResponseEntity.ok("Order cancelled successfully");
    }
}