package com.example.confectionery.dto;

import com.example.confectionery.entity.Nutrition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    @NotBlank(message = "Укажите вкус продукта")
    private String flavor;

    private String description;

    @NotNull(message = "Цена должна быть указана")
    @Positive(message = "Цена должна быть больше нуля")
    private Double price;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    @Min(value = 0, message = "Количество на складе не может быть отрицательным")
    private Integer stockQuantity;

    private List<Long> ingredientIds;
    private Nutrition nutrition;
}