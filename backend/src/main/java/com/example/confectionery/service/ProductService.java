package com.example.confectionery.service;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Category;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Nutrition;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.ProductDtoMapper;
import com.example.confectionery.repository.CategoryRepository;
import com.example.confectionery.repository.IngredientRepository;
import com.example.confectionery.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ProductService {

    private static final String PRODUCT_NOT_FOUND_MSG = "Товар с ID %d не найден в базе данных";
    private static final String INGREDIENT_NOT_FOUND_MSG = "Ингредиент с ID %d не найден";
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private static final String CATEGORY_NOT_FOUND_MSG = "Категория [%s] не найдена";

    public ProductService(ProductRepository productRepository,
                          ProductDtoMapper productDtoMapper,
                          CategoryRepository categoryRepository, IngredientRepository ingredientRepository) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(productDtoMapper)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));
    }

    public List<ProductDto> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryNameIgnoreCase(categoryName).stream()
                .map(productDtoMapper)
                .toList();
    }

    public ProductDto getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productDtoMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Product with name [%s] not found".formatted(name)));
    }

    public ProductDto createProduct(Product product) {
        if (product.getCategory() != null) {
            Category category;
            if (product.getCategory().getId() != null) {

                category = categoryRepository.findById(product.getCategory().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена"));
            } else if (product.getCategory().getName() != null) {

                category = categoryRepository.findByNameIgnoreCase(product.getCategory().getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Категория '" + product.getCategory().getName() + "' не найдена"));
            } else {
                throw new BadRequestException("Категория должна содержать ID или имя");
            }
            product.setCategory(category);
        }

        if (product.getIngredients() != null && !product.getIngredients().isEmpty()) {
            Set<Ingredient> processedIngredients = new HashSet<>();

            for (Ingredient ing : product.getIngredients()) {
                if (ing.getId() != null) {
                    Ingredient existing = ingredientRepository.findById(ing.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MSG.formatted(ing.getId())));
                    processedIngredients.add(existing);
                } else if (ing.getName() != null && !ing.getName().isBlank()) {
                    Ingredient foundOrCreated = ingredientRepository.findByNameIgnoreCase(ing.getName())
                            .orElseGet(() -> {
                                Ingredient newIng = new Ingredient();
                                newIng.setName(ing.getName());
                                newIng.setDescription(ing.getDescription());
                                return ingredientRepository.save(newIng);
                            });
                    processedIngredients.add(foundOrCreated);
                }
            }
            product.setIngredients(processedIngredients);
        }

        Product savedProduct = productRepository.save(product);

        return productDtoMapper.apply(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto details) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));

        product.setName(details.getName());
        product.setPrice(details.getPrice());
        product.setDescription(details.getDescription());
        product.setFlavor(details.getFlavor());

        if (details.getCategory() != null) {
            String catName = details.getCategory();
            Category category = categoryRepository.findByNameIgnoreCase(catName)
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG.formatted(catName)));
            product.setCategory(category);
        }

        if (product.getNutrition() == null) {
            product.setNutrition(new Nutrition());
        }
        product.getNutrition().setWeight(details.getWeight());
        product.getNutrition().setCalories(details.getCalories());

        return productDtoMapper.apply(productRepository.save(product));
    }

    @Transactional
    public ProductDto patchProduct(Long id, ProductDto updatesDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));

        productDtoMapper.updateEntity(updatesDto, product);

        if (updatesDto.getCategory() != null) {
            Category category = categoryRepository.findByNameIgnoreCase(updatesDto.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG.formatted(updatesDto.getCategory())));
            product.setCategory(category);
        }

        return productDtoMapper.apply(productRepository.save(product));
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));

        product.setActive(false);

        productRepository.save(product);

        log.info("Продукт '{}' успешно деактивирован (архивирован)", product.getName());
    }
}