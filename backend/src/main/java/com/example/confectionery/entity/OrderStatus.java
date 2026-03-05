package com.example.confectionery.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Ожидает подтверждения"),
    CONFIRMED("Подтвержден"),
    PROCESSING("Готовится"),
    SHIPPED("Передан курьеру"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен"),
    REFUNDED("Возврат средств");

    private final String displayValue;
}
