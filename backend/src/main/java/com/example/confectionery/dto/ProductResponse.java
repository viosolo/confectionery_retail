package com.example.confectionery.dto;

import com.example.confectionery.entity.Nutrition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private String flavor;
    private String description;
    private String categoryName;
    private List<String> ingredients;
    private Nutrition nutrition;
    private String imageUrl;
    private Integer stockQuantity;
    private boolean active;
}