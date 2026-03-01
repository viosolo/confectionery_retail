package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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