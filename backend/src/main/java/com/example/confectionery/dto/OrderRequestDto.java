package com.example.confectionery.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long userId;
    private List<Long> productIds;
    private String deliveryAddress;
    private String paymentMethod;
    private String notes;
}