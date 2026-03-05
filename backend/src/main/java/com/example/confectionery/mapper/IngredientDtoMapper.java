package com.example.confectionery.mapper;

import com.example.confectionery.dto.IngredientDto;
import com.example.confectionery.entity.Ingredient;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class IngredientDtoMapper implements Function<Ingredient, IngredientDto> {

    @Override
    public IngredientDto apply(Ingredient entity) {
        if (entity == null) {
            return null;
        }
        return IngredientDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public Ingredient toEntity(IngredientDto dto) {
        if (dto == null) {
            return null;
        }
        return Ingredient.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}