package com.example.confectionery.service;

import com.example.confectionery.dto.ProductDTO;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.ProductType;
import com.example.confectionery.mapper.ProductDTOMapper;
import com.example.confectionery.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;

    public ProductService(ProductRepository productRepository,
                          ProductDTOMapper productDTOMapper) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAllProducts()
                .stream()
                .map(productDTOMapper)
                .collect(Collectors.toList()); // Как на втором скрине
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findProductById(id)
                .map(productDTOMapper)
                .orElseThrow(() -> new RuntimeException(
                        "product with id [%s] not found".formatted(id) // Как на третьем скрине
                ));
    }

    public List<ProductDTO> getProductsByType(ProductType type) {
        return productRepository.findAllProducts()
                .stream()
                .filter(product -> product.getType() == type)
                .map(productDTOMapper)
                .collect(Collectors.toList());
    }
    public ProductDTO getProductByName(String name) {
        return productRepository.findByName(name) // Вызываем метод репозитория
                .map(productDTOMapper)            // Превращаем Product в ProductDTO
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
