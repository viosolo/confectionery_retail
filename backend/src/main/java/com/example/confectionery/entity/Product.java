package com.example.confectionery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private ProductType type;
    private String description;
    private String flavor;
    private Double price;
    private Nutrition nutrition;
    private Integer stockQuantity;
}