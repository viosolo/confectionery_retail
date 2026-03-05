package com.example.confectionery.dto;

import com.example.confectionery.entity.Nutrition;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private String categoryName;
    private List<String> ingredients;
    private Nutrition nutrition; // Используем тот же DTO
}