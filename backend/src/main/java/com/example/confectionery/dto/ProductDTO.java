package com.example.confectionery.dto;

public record ProductDTO(
        String name,
        String type,
        String description,
        String flavor,
        Double price,
        Integer weight,
        Integer calories
) {
}