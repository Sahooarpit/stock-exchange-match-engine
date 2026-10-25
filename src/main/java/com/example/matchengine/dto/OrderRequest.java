package com.example.matchengine.dto;

import com.example.matchengine.Side;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private String orderId;
    private String ticker;
    private Side side;
    private long quantity;
    private BigDecimal price;
}