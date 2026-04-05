package com.example.confectionery.mapper;

import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Function;

@Component
public class OrderMapper implements Function<Order, OrderResponseDto> {

    @Override
    public OrderResponseDto apply(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())

                .userName(order.getUser() != null
                        ? (order.getUser().getFirstName() + " " + order.getUser().getLastName()).trim()
                        : (order.getGuestName() != null ? order.getGuestName() : "Guest"))

                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)

                .guestPhone(order.getGuestPhone())

                .productNames(order.getProducts() != null
                        ? order.getProducts().stream().map(Product::getName).toList()
                        : Collections.emptyList())

                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)

                .paymentMethod(order.getPaymentMethod() != null
                        ? order.getPaymentMethod().name()
                        : null)
                .paymentMethodName(order.getPaymentMethod() != null
                        ? order.getPaymentMethod().getDisplayValue()
                        : "Not specified")

                .deliveryAddress(order.getDeliveryAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .build();
    }
}