package com.example.confectionery.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // Этот класс не имеет своей таблицы, он "встраивается" в Product
public class Nutrition {
    private Integer weight;
    private Integer calories;
}