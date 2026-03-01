package com.example.confectionery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private String userName;      // Склеенное имя
    private List<String> productNames; // Просто названия товаров
    private BigDecimal totalAmount;
    private String status;
    private String createdAt;
}