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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("Test: Update Relations - Some Ingredients Missing (Warn Path)")
    void updateProductRelations_IngredientsPartial() {
        Product product = new Product();
        product.setIngredients(new java.util.HashSet<>());

        ProductRequest request = new ProductRequest();
        request.setIngredientIds(java.util.List.of(1L, 2L));


        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(product));

        Ingredient mockIngredient = new Ingredient();

        when(ingredientRepository.findAllById(anyList())).thenReturn(java.util.List.of(mockIngredient));

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());


        productService.patchProduct(1L, request);

        assertEquals(1, product.getIngredients().size(), "Должен быть добавлен 1 найденный ингредиент");
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
        otherProduct.setId(2L); // Другой ID!
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
    void updateProductRelations_ShouldLogWarning_WhenSomeIngredientsNotFound() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Test Product");
        existingProduct.setIngredients(new HashSet<>());

        ProductRequest request = new ProductRequest();
        List<Long> requestedIds = List.of(1L, 2L);
        request.setIngredientIds(requestedIds);

        Ingredient foundIngredient = new Ingredient();
        foundIngredient.setId(1L);


        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(ingredientRepository.findAllById(requestedIds))
                .thenReturn(List.of(foundIngredient));
        when(productRepository.save(any())).thenReturn(existingProduct);
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        productService.updateProduct(1L, request);

        verify(ingredientRepository).findAllById(requestedIds);

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
    @DisplayName("Test: createProductsBulk - Throws AlreadyExistsException when name taken")
    void createProductsBulk_ThrowsException_IfNameExists() {

        ProductRequest request = new ProductRequest();
        request.setName("Existing Cake");

        List<ProductRequest> requests = List.of(request);

        when(productRepository.findByName("Existing Cake"))
                .thenReturn(Optional.of(new Product()));

        assertThrows(AlreadyExistsException.class, () -> productService.createProductsBulk(requests));
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
    @DisplayName("Test: Bulk creation using Stream API mapping")
    void createProductsBulk_Success() {
        ProductRequest r1 = new ProductRequest(); r1.setName("Product 1");
        ProductRequest r2 = new ProductRequest(); r2.setName("Product 2");

        when(productRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productDtoMapper.apply(any())).thenReturn(new ProductResponse());

        List<ProductResponse> results = productService.createProductsBulk(List.of(r1, r2));

        assertEquals(2, results.size());
        verify(productRepository, times(2)).save(any());
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

