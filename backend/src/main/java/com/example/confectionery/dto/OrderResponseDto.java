package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String orderNumber;

    private String userName;
    private String userEmail;

    private List<String> productNames;

    private BigDecimal totalAmount;

    private String status;
    private String statusName;

    private String paymentMethod;
    private String paymentMethodName;

    private String deliveryAddress;
    private String notes;

    private LocalDateTime createdAt;
}