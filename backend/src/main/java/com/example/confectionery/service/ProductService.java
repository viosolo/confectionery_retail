package com.example.confectionery.service;

import com.example.confectionery.dto.ProductRequest;
import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.dto.ProductSearchKey;
import com.example.confectionery.entity.Category;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.ProductDtoMapper;
import com.example.confectionery.repository.CategoryRepository;
import com.example.confectionery.repository.IngredientRepository;
import com.example.confectionery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCT_NOT_FOUND_MSG = "Товар с ID %d не найден";
    private static final String CATEGORY_NOT_FOUND_MSG = "Категория с ID %d не найдена";

    private final Map<ProductSearchKey, Page<ProductResponse>> searchIndex = new ConcurrentHashMap<>();
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(productDtoMapper)
                .toList();
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productDtoMapper)
                .toList();
    }


    public Page<ProductResponse> searchWithCache(String slug, List<String> flavors, Double maxPrice, Pageable pageable) {

        String sortString = pageable.getSort().toString();

        ProductSearchKey key = new ProductSearchKey(
                slug,
                flavors,
                maxPrice,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortString
        );

        if (searchIndex.containsKey(key)) {
            log.info(">>> [CACHE HIT] Данные взяты из памяти. Ключ: {}, Сортировка: {}", slug, sortString);
            return searchIndex.get(key);
        }

        log.info(">>> [DB QUERY] Запрос к базе данных для категории: {}", slug);

        Page<Product> productPage = productRepository.findByComplexFilters(slug, flavors, maxPrice, pageable);

        Page<ProductResponse> responsePage = productPage.map(productDtoMapper);

        searchIndex.put(key, responsePage);

        return responsePage;
    }

    public ProductResponse getProductByName(String name) {
        return productRepository.findByName(name)
                .map(productDtoMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Product with name [%s] not found".formatted(name)));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setActive(true);

        updateProductFields(product, request);
        Product savedProduct = productRepository.save(product);
        invalidateCache();
        return productDtoMapper.apply(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));
        updateProductFields(product, request);
        invalidateCache();
        return productDtoMapper.apply(productRepository.save(product));
    }

    private void updateProductFields(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setFlavor(request.getFlavor());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());

        updateProductRelations(product, request);
    }

    @Transactional
    public ProductResponse patchProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getFlavor() != null) {
            product.setFlavor(request.getFlavor());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        updateProductRelations(product, request);
        invalidateCache();
        return productDtoMapper.apply(productRepository.save(product));
    }

    private void updateProductRelations(Product product, ProductRequest request) {
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG.formatted(request.getCategoryId())));
            product.setCategory(category);
        }

        if (request.getIngredientIds() != null) {
            List<Ingredient> ingredients = ingredientRepository.findAllById(request.getIngredientIds());
            product.getIngredients().clear();
            product.getIngredients().addAll(ingredients);
        }

        if (request.getNutrition() != null) {
            product.setNutrition(request.getNutrition());
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id)));
        product.setActive(false);
        productRepository.save(product);
        log.info("Продукт '{}' деактивирован", product.getName());
        invalidateCache();
    }

    public void invalidateCache() {

        searchIndex.clear();
    }
}