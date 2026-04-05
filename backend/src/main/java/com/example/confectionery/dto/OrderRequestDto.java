package com.example.confectionery.dto;

import com.example.confectionery.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

    private Long userId;

    @NotEmpty(message = "Корзина пуста. Добавьте хотя бы один товар")
    private List<Long> productIds;

    @NotBlank(message = "Пожалуйста, укажите адрес доставки")
    private String deliveryAddress;

    @Size(max = 100, message = "Имя слишком длинное")
    private String guestName;

    @Pattern(regexp = "^(\\+375|80)(29|25|44|33)(\\d{7})$",
            message = "Введите корректный номер телефона (например, +375291234567)")
    private String guestPhone;

    @NotNull(message = "Выберите способ оплаты (Наличные или Карта)")
    private PaymentMethod paymentMethod;

    @Size(max = 500)
    private String notes;
}