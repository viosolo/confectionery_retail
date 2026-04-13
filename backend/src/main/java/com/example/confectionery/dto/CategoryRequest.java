package com.example.confectionery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(max = 50, message = "Название должно быть до 50 символов")
    private String name;

    @NotBlank(message = "Slug не может быть пустым")
    @Pattern(regexp = "^[a-zа-я0-9-]+$", message = "Slug может содержать латинские и русские строчные буквы, цифры и дефис")
    private String slug;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 10, max = 500, message = "Описание должно быть содержательным (от 10 до 500 символов)")
    private String description;

    private String imageUrl;
}