package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Сделает getName(), setName(), toString() и т.д.
@NoArgsConstructor // Пустой конструктор для Jackson
@AllArgsConstructor // Конструктор для Builder
@Builder // Позволит создавать: ProductDto.builder().name("Торт").build()
public class ProductDto {

    private Long id;
    private String name;
    private String category;
    private String description;
    private String flavor;
    private Double price;
    private Integer weight;
    private Integer calories;
}