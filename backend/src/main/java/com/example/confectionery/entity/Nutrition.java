package com.example.confectionery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Nutrition {

    @NotNull(message = "Укажите вес продукта")
    @Positive(message = "Вес должен быть больше нуля")
    @Column(nullable = false)
    private Integer weight;

    @NotNull(message = "Укажите количество калорий")
    @PositiveOrZero(message = "Калории не могут быть отрицательными")
    @Column(nullable = false)
    private Integer calories;
}