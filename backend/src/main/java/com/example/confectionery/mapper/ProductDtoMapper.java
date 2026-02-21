package com.example.confectionery.mapper;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Product;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper implements Function<Product, ProductDto> {

  @Override
  public ProductDto apply(Product product) {
    return new ProductDto(
        product.getName(),
        product.getType().name(),
        product.getDescription(),
        product.getFlavor(),
        product.getPrice(),
        product.getNutrition().weight(),
        product.getNutrition().calories()
    );
  }
}