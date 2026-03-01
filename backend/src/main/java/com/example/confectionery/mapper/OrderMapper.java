package com.example.confectionery.mapper;

import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDto toResponseDTO(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserName(order.getUserName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt().toString());

        dto.setProductNames(order.getProducts().stream()
                .map(Product::getName)
                .toList());

        return dto;
    }
}