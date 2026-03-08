package com.example.confectionery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithProduct {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Page<ProductResponse> products;
}
