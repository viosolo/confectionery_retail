package com.example.confectionery.mapper;

import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductDtoMapper implements Function<Product, ProductResponse> {

    @Override
    public ProductResponse apply(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .flavor(product.getFlavor())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .ingredients(product.getIngredients().stream()
                        .map(Ingredient::getName)
                        .toList())
                .nutrition(product.getNutrition())
                .build();
    }
}