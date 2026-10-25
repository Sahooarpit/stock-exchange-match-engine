package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.ClientService;
import com.example.matchengine.MatchEngine;
import com.example.matchengine.Order;
import com.example.matchengine.Trade;
import com.example.matchengine.dto.OrderRequest;
import com.example.matchengine.repository.OrderRepository;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TradeController {

    private final MatchEngine matchEngine;
    private final ClientService clientService;
    private final OrderRepository orderRepository;

    @PostMapping("/trade")
    public ResponseEntity<String> submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // 1. Check for Idempotency
        if (orderRequest.getOrderId() != null && !orderRequest.getOrderId().trim().isEmpty()) {
            Optional<Order> existingOrderOpt = orderRepository.findById(orderRequest.getOrderId());
            if (existingOrderOpt.isPresent()) {
                Order existingOrder = existingOrderOpt.get();
                // Security check to ensure a user isn't guessing someone else's order ID
                if (!existingOrder.getClient().getUsername().equals(username)) {
                    throw new IllegalStateException("Order ID collision.");
                }
                return ResponseEntity.ok("Order already processed (Idempotent response).");
            }
        }

        Client client = clientService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));

        Order order = new Order(
                client,
                orderRequest.getTicker(),
                orderRequest.getSide(),
                orderRequest.getQuantity(),
                orderRequest.getPrice(),
                orderRequest.getOrderId()
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