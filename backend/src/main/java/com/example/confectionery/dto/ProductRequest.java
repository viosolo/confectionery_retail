package com.example.confectionery.dto;

import com.example.confectionery.entity.Nutrition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    @NotNull(message = "Цена должна быть указана")
    @Positive(message = "Цена должна быть больше нуля")
    private Double price;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private List<Long> ingredientIds;
    private Nutrition nutrition;
}