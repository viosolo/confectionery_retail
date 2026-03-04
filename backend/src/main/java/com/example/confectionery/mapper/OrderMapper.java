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

        if (order.getUser() != null) {
            String fullName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
            dto.setUserName(fullName);
            dto.setUserEmail(order.getUser().getEmail());
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());

        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNotes(order.getNotes());

        if (order.getCreatedAt() != null) {
            dto.setCreatedAt(order.getCreatedAt().toString());
        }

        dto.setProductNames(order.getProducts().stream()
                .map(Product::getName)
                .toList());

        return dto;
    }
}