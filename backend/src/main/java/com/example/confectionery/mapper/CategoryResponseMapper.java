package com.example.confectionery.mapper;

import com.example.confectionery.dto.CategoryResponse;
import com.example.confectionery.entity.Category;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class CategoryResponseMapper implements Function<Category, CategoryResponse> {

    @Override
    public CategoryResponse apply(Category category) {
        if (category == null) {
            return null;

        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }
}