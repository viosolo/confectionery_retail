package com.example.confectionery.dto;

import com.example.confectionery.entity.Nutrition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Название продукта не может быть пустым")
    @Size(max = 100, message = "Название слишком длинное")
    private String name;

    @NotBlank(message = "Укажите вкус продукта")
    private String flavor;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotNull(message = "Цена должна быть указана")
    @Positive(message = "Цена должна быть больше нуля")
    private Double price;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    @NotNull(message = "Укажите количество на складе")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer stockQuantity;

    @NotNull(message = "Укажите ссылку на фото")
    private String imageUrl;

    @NotEmpty(message = "Укажите хотя бы один ингредиент")
    private List<Long> ingredientIds;

    @NotNull(message = "Данные о пищевой ценности обязательны")
    @Valid
    private Nutrition nutrition;
}