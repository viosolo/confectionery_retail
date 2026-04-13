package com.example.confectionery.mapper;

import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OrderMapper implements Function<Order, OrderResponseDto> {

    private final ProductDtoMapper productMapper;

    @Override
    public OrderResponseDto apply(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userName(resolveUserName(order))
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .guestPhone(resolvePhone(order))
                .products(mapProducts(order))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .statusName(order.getStatus() != null ? order.getStatus().getDisplayValue() : "Новый")
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .paymentMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().getDisplayValue() : "Не указан")
                .deliveryAddress(order.getDeliveryAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private String resolveUserName(Order order) {
        if (order.getUser() != null) {
            return (order.getUser().getFirstName() + " " + order.getUser().getLastName()).trim();
        }
        return order.getGuestName() != null ? order.getGuestName() : "Guest";
    }

    private String resolvePhone(Order order) {
        if (order.getUser() != null && order.getUser().getPhone() != null) {
            return order.getUser().getPhone();
        }
        return order.getGuestPhone() != null ? order.getGuestPhone() : "—";
    }

    private List<ProductResponse> mapProducts(Order order) {
        if (order.getProducts() == null) {
            return Collections.emptyList();
        }
        return order.getProducts().stream()
                .map(productMapper)
                .toList();
    }
}