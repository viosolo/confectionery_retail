package com.example.confectionery.service;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Category;
import com.example.confectionery.entity.Nutrition;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.ProductDtoMapper;
import com.example.confectionery.repository.CategoryRepository;
import com.example.confectionery.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          ProductDtoMapper productDtoMapper,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(productDtoMapper)
                .orElseThrow(() -> new RuntimeException("Product with id [%s] not found".formatted(id)));
    }

    public List<ProductDto> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCase(categoryName).stream()
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productDtoMapper)
                .orElseThrow(() -> new RuntimeException("Product with name [%s] not found".formatted(name)));
    }

    public ProductDto createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getName() != null) {
            String catName = product.getCategory().getName();

            Category category = categoryRepository.findByNameIgnoreCase(catName)
                    .orElseThrow(() -> new RuntimeException("Категория '" + catName + "' не найдена. Создайте её сначала!"));

            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);

        return productDtoMapper.apply(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, Product details) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар с id " + id + " не найден в бд"));

        product.setName(details.getName());
        product.setPrice(details.getPrice());
        product.setStockQuantity(details.getStockQuantity());
        product.setDescription(details.getDescription());
        product.setFlavor(details.getFlavor());

        if (details.getCategory() != null && details.getCategory().getName() != null) {
            String categoryName = details.getCategory().getName();

            Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                    .orElseThrow(() -> new ResourceNotFoundException("Товар с ID " + id + " не найден"));

            category.addProduct(product);
        }

        if (details.getNutrition() != null) {
            if (product.getNutrition() == null) {
                product.setNutrition(new Nutrition());
            }
            product.getNutrition().setWeight(details.getNutrition().getWeight());
            product.getNutrition().setCalories(details.getNutrition().getCalories());
        }

        Product savedProduct = productRepository.save(product);
        return productDtoMapper.apply(savedProduct);
    }


    @Transactional
    public ProductDto patchProduct(Long id, ProductDto updatesDto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар с ID " + id + " не найден"));

        productDtoMapper.updateEntity(updatesDto, product);

        if (updatesDto.getCategory() != null) {
            Category category = categoryRepository.findByNameIgnoreCase(updatesDto.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Категория с ID " + id + " не найдена"));
            product.setCategory(category);
        }

        return productDtoMapper.apply(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}