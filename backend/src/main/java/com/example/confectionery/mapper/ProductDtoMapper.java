package com.example.confectionery.mapper;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductDtoMapper implements Function<Product, ProductDto> {

    @Override
    public ProductDto apply(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : "Без категории",
                product.getDescription(),
                product.getFlavor(),
                product.getPrice(),
                // Заменяем weight() на getWeight() и добавляем проверку на null
                product.getNutrition() != null ? product.getNutrition().getWeight() : 0,
                product.getNutrition() != null ? product.getNutrition().getCalories() : 0
        );
    }

    public void updateEntity(ProductDto dto, Product product) {
        // 1. Проверяем строки на null
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getFlavor() != null) product.setFlavor(dto.getFlavor());

        // 2. Для чисел (Double/Integer) проверяем и на null, и на значение > 0
        // Это важно, чтобы случайно не обнулить цену, если она не пришла в JSON
        if (dto.getPrice() != null && dto.getPrice() > 0) {
            product.setPrice(dto.getPrice());
        }

        // 3. Обновляем вложенный объект Nutrition (Пищевая ценность)
        if (product.getNutrition() != null) {
            if (dto.getWeight() != null && dto.getWeight() > 0) {
                product.getNutrition().setWeight(dto.getWeight());
            }
            if (dto.getCalories() != null && dto.getCalories() > 0) {
                product.getNutrition().setCalories(dto.getCalories());
            }
        }
    }
}