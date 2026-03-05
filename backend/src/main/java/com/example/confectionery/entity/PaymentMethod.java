package com.example.confectionery.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // Создаст конструктор для поля displayValue
public enum PaymentMethod {
    CASH("Наличными при получении"),
    CARD_ON_DELIVERY("Картой курьеру"),
    ONLINE_PAYMENT("Оплата на сайте");

    private final String displayValue;
}