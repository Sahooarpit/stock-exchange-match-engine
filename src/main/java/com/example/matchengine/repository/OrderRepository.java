package com.example.matchengine.repository;

import com.example.matchengine.Order;
import com.example.matchengine.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByTickerAndStatusInOrderByPriceDescCreatedAtAsc(String ticker, List<OrderStatus> statuses);
    List<Order> findByTickerAndStatusInOrderByPriceAscCreatedAtAsc(String ticker, List<OrderStatus> statuses);

    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);
    List<Order> findByClient_ClientIdAndStatusIn(String clientId, List<OrderStatus> statuses);
}