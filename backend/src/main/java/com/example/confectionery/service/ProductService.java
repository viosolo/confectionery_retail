package com.example.confectionery.service;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.ProductType;
import com.example.confectionery.mapper.ProductDtoMapper;
import com.example.confectionery.repository.ProductRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;

    public ProductService(ProductRepository productRepository,
                          ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAllProducts()
                .stream()
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        return productRepository.findProductById(id)
                .map(productDtoMapper)
                .orElseThrow(() -> new RuntimeException(
                        "product with id [%s] not found".formatted(id)
                ));
    }

    public List<ProductDto> getProductsByType(ProductType type) {
        return productRepository.findAllProducts()
                .stream()
                .filter(product -> product.getType() == type)
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productDtoMapper)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
