package com.example.confectionery.service;

import com.example.confectionery.dto.ProductRequest;
import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.ProductCache;
import com.example.confectionery.exception.AlreadyExistsException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.ProductDtoMapper;
import com.example.confectionery.repository.CategoryRepository;
import com.example.confectionery.repository.IngredientRepository;
import com.example.confectionery.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductDtoMapper productDtoMapper;
    @Mock private CategoryRepository categoryRepository;
    @Mock private IngredientRepository ingredientRepository;
    @Mock private ProductCache searchIndex;

    @InjectMocks private ProductService productService;

    @Test
    @DisplayName("Test: Get all active products using Stream")
    void getAllProducts_Success() {
        when(productRepository.findAllByActiveTrue()).thenReturn(List.of(new Product()));
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(1, result.size());
        verify(productRepository).findAllByActiveTrue();
    }

    @Test
    void getProductById_Success() {
        Long productId = 5L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Зефир");

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setName("Зефир");

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productDtoMapper.apply(mockProduct)).thenReturn(mockResponse);

        ProductResponse result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("Зефир", result.getName());
        verify(productRepository, times(1)).findById(productId);
        verify(productDtoMapper, times(1)).apply(mockProduct);
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        Long productId = 99L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.getProductById(productId));

        assertEquals("Продукт с ID " + productId + " не найден", exception.getMessage());

        verifyNoInteractions(productDtoMapper);
    }
    @Test
    @DisplayName("Test: Create product with duplicate name check (Optional)")
    void createProduct_DuplicateException() {
        ProductRequest request = new ProductRequest();
        request.setName("Existing macaron with cherry");
        when(productRepository.findByName("Existing macaron with cherry")).thenReturn(Optional.of(new Product()));

        assertThrows(AlreadyExistsException.class, () -> productService.createProduct(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: Get product by name (Optional usage)")
    void getProductByName_Success() {
        String name = "Chocolate Macaron";
        Product product = new Product();
        product.setName(name);

        when(productRepository.findByName(name)).thenReturn(Optional.of(product));
        when(productDtoMapper.apply(product)).thenReturn(new ProductResponse());

        ProductResponse response = productService.getProductByName(name);

        assertNotNull(response);
        verify(productRepository).findByName(name);
    }

    @Test
    @DisplayName("Test: Get product by name - Not Found")
    void getProductByName_NotFound() {

        when(productRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductByName("Unknown"));
    }

    @Test
    @DisplayName("Test: Update Product - Resource Not Found")
    void updateProduct_NotFound() {
        Long nonExistentId = 99L;
        ProductRequest emptyRequest = new ProductRequest();

        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(nonExistentId, emptyRequest)
        );
    }

    @Test
    @DisplayName("Test: Update Relations - Category Not Found")
    void updateProduct_CategoryNotFound() {

        Long id = 1L;
        Product existing = new Product();
        ProductRequest request = new ProductRequest();
        request.setCategoryId(999L);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(id, request));
    }

    @Test
    @DisplayName("Test: Get products by category using Stream")
    void getProductsByCategoryId_Success() {
        Long catId = 1L;
        when(productRepository.findByCategoryId(catId)).thenReturn(List.of(new Product()));
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        List<ProductResponse> result = productService.getProductsByCategoryId(catId);

        assertEquals(1, result.size());
        verify(productRepository).findByCategoryId(catId);
    }

    @Test
    @DisplayName("Test: Search - Cache Miss (Database Query)")
    void searchWithCache_Miss() {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(List.of(new Product()));


        when(searchIndex.containsKey(any())).thenReturn(false);
        when(productRepository.findByComplexFilters(any(), any(), any(), any())).thenReturn(productPage);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        var result = productService.searchWithCache("test", null, 100.0, pageable);

        assertNotNull(result);
        verify(searchIndex).put(any(), any());
    }

    @Test
    @DisplayName("Test: Update Relations - Logs warning when sizes differ")
    void updateProductRelations_ShouldLogWarning_WhenSizesMismatch() {

        Product product = Product.builder()
                .id(1L)
                .name("Vanilla")
                .ingredients(new HashSet<>())
                .build();

        ProductRequest request = new ProductRequest();
        request.setIngredientIds(List.of(100L, 200L));

        Ingredient foundOne = new Ingredient();
        foundOne.setId(100L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(ingredientRepository.findAllById(anyCollection())).thenReturn(List.of(foundOne));
        when(productRepository.save(any())).thenReturn(product);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.patchProduct(1L, request);

        verify(ingredientRepository).findAllById(anyCollection());
        assertEquals(1, product.getIngredients().size());
    }

    @Test
    @DisplayName("Test: Update Relations - No warning when all ingredients found")
    void updateProductRelations_ShouldNotLog_WhenSizesMatch() {

        Product product = Product.builder()
                .id(1L)
                .name("Vanilla")
                .ingredients(new HashSet<>())
                .build();

        ProductRequest request = new ProductRequest();
        request.setIngredientIds(List.of(100L));

        Ingredient foundOne = new Ingredient();
        foundOne.setId(100L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(ingredientRepository.findAllById(anyCollection())).thenReturn(List.of(foundOne));
        when(productRepository.save(any())).thenReturn(product);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.patchProduct(1L, request);

        assertEquals(1, product.getIngredients().size());
    }

    @Test
    @DisplayName("Test: Patch product name - Success (Unique Name)")
    void patchProduct_NameSuccess() {
        Long id = 1L;
        Product existing = new Product();
        existing.setId(id);
        existing.setName("Old Name");

        ProductRequest request = new ProductRequest();
        request.setName("New Unique Name");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.findByName("New Unique Name")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(existing);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.patchProduct(id, request);

        assertEquals("New Unique Name", existing.getName());
        verify(productRepository).findByName("New Unique Name");
    }
    @Test
    @DisplayName("Test: Patch product with its own name - Success")
    void patchProduct_SameName_Success() {
        Long id = 1L;
        String currentName = "Original Name";

        Product existing = new Product();
        existing.setId(id);
        existing.setName(currentName);

        ProductRequest request = new ProductRequest();
        request.setName(currentName);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        when(productRepository.findByName(currentName)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.patchProduct(id, request);

        verify(productRepository).findByName(currentName);
        verify(productRepository).save(existing);
    }


    @Test
    @DisplayName("Test: Patch product name - Fails (Name already taken)")
    void patchProduct_NameExistsException() {
        Long id = 1L;
        Product existing = new Product();
        existing.setId(id);

        Product otherProduct = new Product();
        otherProduct.setId(2L);
        otherProduct.setName("Busy Name");

        ProductRequest request = new ProductRequest();
        request.setName("Busy Name");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        when(productRepository.findByName("Busy Name")).thenReturn(Optional.of(otherProduct));

        assertThrows(AlreadyExistsException.class, () -> productService.patchProduct(id, request));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: Search - Cache Hit (Return from Memory)")
    void searchWithCache_Hit() {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<ProductResponse> cachedPage = new org.springframework.data.domain.PageImpl<>(List.of(new ProductResponse()));


        when(searchIndex.containsKey(any())).thenReturn(true);
        when(searchIndex.get(any())).thenReturn(cachedPage);

        var result = productService.searchWithCache("test", null, 100.0, pageable);

        assertEquals(cachedPage, result);
        verify(productRepository, never()).findByComplexFilters(any(), any(), any(), any());
    }



    @Test
    @DisplayName("Test: Soft delete (deactivation) logic")
    void deleteProduct_Success() {
        Long id = 1L;
        Product product = new Product();
        product.setActive(true);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteProduct(id);

        assertFalse(product.isActive());
        verify(productRepository).save(product);
        verify(searchIndex).clear();
    }

    @Test
    @DisplayName("Bulk: Transactional - Success path")
    void createBulkTransactional_Success() {

        ProductRequest request = new ProductRequest();
        request.setName("Unique Bulk Cake");
        List<ProductRequest> requests = List.of(request);

        when(productRepository.existsByName("Unique Bulk Cake")).thenReturn(false);
        when(productRepository.save(any())).thenReturn(new Product());
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        var result = productService.createBulkTransactional(requests);

        assertEquals(1, result.size());
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("Bulk: Non-Transactional - Success path")
    void createBulkNonTransactional_Success() {

        ProductRequest request = new ProductRequest();
        request.setName("Another Unique Cake");
        List<ProductRequest> requests = List.of(request);

        when(productRepository.existsByName("Another Unique Cake")).thenReturn(false);
        when(productRepository.save(any())).thenReturn(new Product());
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        var result = productService.createBulkNonTransactional(requests);

        assertEquals(1, result.size());
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("Bulk: Common Logic - Throws Exception if name exists")
    void createBulk_ThrowsException_IfNameExists() {

        ProductRequest request = new ProductRequest();
        request.setName("Existing Cake");
        List<ProductRequest> requests = List.of(request);

        when(productRepository.existsByName("Existing Cake")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () ->
                productService.createBulkTransactional(requests)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: Full update of product fields")
    void updateProduct_Success() {
        Long id = 1L;
        Product existing = new Product();
        ProductRequest request = new ProductRequest();
        request.setName("New Name vanilla macaron");
        request.setPrice(500.0);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.updateProduct(id, request);

        assertEquals("New Name vanilla macaron", existing.getName());
        assertEquals(500.0, existing.getPrice());
        verify(searchIndex).clear();
    }


    @Test
    @DisplayName("Test: Create product successfully when name is unique")
    void createProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("New zephyr");
        request.setPrice(150.0);

        when(productRepository.findByName("New zephyr")).thenReturn(Optional.empty());

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        ProductResponse result = productService.createProduct(request);
        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
        verify(searchIndex).clear();
    }


    @Test
    @DisplayName("Test: Partial update using Optional.ifPresent logic")
    void patchProduct_ShouldOnlyUpdateProvidedFields() {
        Long id = 1L;
        Product existing = new Product();
        existing.setName("Original");
        existing.setPrice(10.0);

        ProductRequest patch = new ProductRequest();
        patch.setPrice(99.0);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.patchProduct(id, patch);

        assertEquals(99.0, existing.getPrice());
        assertEquals("Original", existing.getName());
    }

}

