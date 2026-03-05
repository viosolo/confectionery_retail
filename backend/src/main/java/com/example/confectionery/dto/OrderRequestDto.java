package com.example.confectionery.dto;

import com.example.confectionery.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

    @NotNull(message = "Техническая ошибка: ID пользователя не передан")
    private Long userId;

    @NotEmpty(message = "Корзина пуста. Добавьте хотя бы один товар")
    private List<Long> productIds;

    @NotBlank(message = "Пожалуйста, укажите адрес доставки")
    private String deliveryAddress;

    @NotNull(message = "Выберите способ оплаты (Наличные или Карта)")
    private PaymentMethod paymentMethod;

    private String notes;
}