package com.example.confectionery.service;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Category;
import com.example.confectionery.entity.Nutrition;
import com.example.confectionery.entity.Product;
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
    private final CategoryRepository categoryRepository; // 1. Поле на месте

    public ProductService(ProductRepository productRepository,
                          ProductDtoMapper productDtoMapper,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
        this.categoryRepository = categoryRepository; // Теперь Spring подставит репозиторий сюда
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
        // 1. Проверяем, передана ли категория в запросе
        if (product.getCategory() != null && product.getCategory().getName() != null) {
            String catName = product.getCategory().getName();

            // 2. Ищем её в базе по имени (как в методе update)
            Category category = categoryRepository.findByNameIgnoreCase(catName)
                    .orElseThrow(() -> new RuntimeException("Категория '" + catName + "' не найдена. Создайте её сначала!"));

            // 3. Привязываем найденную категорию к новому товару
            product.setCategory(category);
        }

        // 4. Сохраняем товар (Nutrition сохранится автоматически, если он есть в объекте)
        Product savedProduct = productRepository.save(product);

        // 5. Возвращаем красивый DTO
        return productDtoMapper.apply(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, Product details) {
        // 1. Ищем товар в базе
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар с id " + id + " не найден"));

        // 2. Обновляем основные поля
        product.setName(details.getName());
        product.setPrice(details.getPrice());
        product.setStockQuantity(details.getStockQuantity());
        product.setDescription(details.getDescription());
        product.setFlavor(details.getFlavor());

        // 3. ПРИВЯЗКА КАТЕГОРИИ ПО ИМЕНИ (Твоя идея)
        if (details.getCategory() != null && details.getCategory().getName() != null) {
            String categoryName = details.getCategory().getName();

            // Ищем в базе категорию с таким именем
            Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                    .orElseThrow(() -> new RuntimeException("Категория '" + categoryName + "' не существует в БД"));

            // Используем твой метод для связи
            category.addProduct(product);
        }

        // 4. Обновляем Nutrition (с проверками)
        if (details.getNutrition() != null) {
            if (product.getNutrition() == null) {
                product.setNutrition(new Nutrition());
            }
            product.getNutrition().setWeight(details.getNutrition().getWeight());
            product.getNutrition().setCalories(details.getNutrition().getCalories());
        }

        // 5. Сохраняем и превращаем в DTO через твой маппер
        Product savedProduct = productRepository.save(product);
        return productDtoMapper.apply(savedProduct);
    }


    @Transactional
    public ProductDto patchProduct(Long id, ProductDto updatesDto) {
        // 1. Находим товар в базе
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // 2. Используем наш маппер, чтобы обновить только пришедшие поля
        productDtoMapper.updateEntity(updatesDto, product);

        // 3. Если в DTO пришло имя категории — обновляем её отдельно
        // Проверяем имя категории через геттер
        if (updatesDto.getCategory() != null) {
            Category category = categoryRepository.findByNameIgnoreCase(updatesDto.getCategory())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            product.setCategory(category);
        }
        // 4. Сохраняем и возвращаем результат через тот же маппер
        return productDtoMapper.apply(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}