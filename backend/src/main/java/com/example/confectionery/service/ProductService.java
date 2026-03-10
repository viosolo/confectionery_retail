package com.example.confectionery.service;

import com.example.confectionery.dto.ProductRequest;
import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.dto.ProductSearchKey;
import com.example.confectionery.entity.Category;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.ProductCache;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String LOG_SUCCESS_ACTION = ">>> Product ID {} {} successfully";
    private static final String LOG_ERROR_NOT_FOUND = ">>> {} failed: Product ID {} not found";
    private static final String PRODUCT_NOT_FOUND_MSG = "Товар с ID %d не найден";
    private static final String CATEGORY_NOT_FOUND_MSG = "Категория с ID %d не найдена";
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    private final ProductCache searchIndex;

    public List<ProductResponse> getAllProducts() {
        log.info(">>> Fetching all active products");
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
            log.info(">>> [CACHE HIT] Returning data from memory for slug: {}", slug);
            return searchIndex.get(key);
        }

        log.info(">>> [DB QUERY] for slug: {}", slug);

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

        log.info(">>> Creating new product: {}", request.getName());
        Product product = new Product();
        product.setActive(true);

        updateProductFields(product, request);
        Product savedProduct = productRepository.save(product);
        log.info(">>> Product '{}' created successfully with ID: {}", savedProduct.getName(), savedProduct.getId());
        invalidateCache();
        return productDtoMapper.apply(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id, "Update");

        updateProductFields(product, request);

        invalidateCache();

        Product saved = productRepository.save(product);
        logSuccess(id, "updated");
        return productDtoMapper.apply(saved);
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
        log.info(">>> Attempting partial update (patch) for product ID: {}", id);

        Product product = findProductOrThrow(id, "Patch");

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

        Product saved = productRepository.save(product);
        logSuccess(id, "partially updated");
        return productDtoMapper.apply(saved);
    }

    private void updateProductRelations(Product product, ProductRequest request) {
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {

                        log.error(">>> Relation error: Category ID {} not found", request.getCategoryId());
                        return new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG.formatted(request.getCategoryId()));
                    });
            product.setCategory(category);
        }

        if (request.getIngredientIds() != null) {
            log.debug(">>> Updating ingredients for product: {}", product.getName());
            List<Ingredient> ingredients = ingredientRepository.findAllById(request.getIngredientIds());

            if (ingredients.size() != request.getIngredientIds().size()) {
                log.warn(">>> Some ingredient IDs were not found in database for product: {}", product.getName());
            }

            product.getIngredients().clear();
            product.getIngredients().addAll(ingredients);
        }

        if (request.getNutrition() != null) {
            product.setNutrition(request.getNutrition());
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info(">>> Attempting to deactivate product ID: {}", id);
        Product product = findProductOrThrow(id, "Deactivation");
        product.setActive(false);
        productRepository.save(product);
        logSuccess(id, "deactivated");
        invalidateCache();
    }

    public void invalidateCache() {
        log.info(">>> [CACHE INVALIDATED] Search index cleared due to product updates");
        searchIndex.clear();
    }

    private Product findProductOrThrow(Long id, String actionName) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(LOG_ERROR_NOT_FOUND, actionName, id);
                    return new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG.formatted(id));
                });
    }

    private void logSuccess(Long id, String action) {
        log.info(LOG_SUCCESS_ACTION, id, action);
    }
}