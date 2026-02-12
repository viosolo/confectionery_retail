package com.example.confectionery.mapper;

import com.example.confectionery.dto.ProductDTO;
import com.example.confectionery.entity.Product;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class ProductDTOMapper implements Function<Product, ProductDTO> {

    @Override
    public ProductDTO apply(Product product) {
        return new ProductDTO(
                product.getName(),
                product.getType().name(),
                product.getDescription(),
                product.getFlavor(),
                product.getPrice(),
                product.getWeight(),
                product.getCalories()
        );
    }
}