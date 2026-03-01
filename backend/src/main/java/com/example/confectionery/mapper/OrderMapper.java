package com.example.confectionery.mapper;

import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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

        // Мапим список продуктов в список их названий
        dto.setProductNames(order.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList()));

        return dto;
    }
}