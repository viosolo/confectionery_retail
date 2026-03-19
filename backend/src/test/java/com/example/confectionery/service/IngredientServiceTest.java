package com.example.confectionery.service;

import com.example.confectionery.dto.IngredientDto;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.IngredientDtoMapper;
import com.example.confectionery.repository.IngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ProductService productService;

    @Mock
    private IngredientDtoMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    @DisplayName("getAll - Success")
    void getAll_Success() {
        Ingredient ingredient = new Ingredient();
        when(ingredientRepository.findAll()).thenReturn(List.of(ingredient));
        when(ingredientMapper.apply(any())).thenReturn(new IngredientDto());

        List<IngredientDto> result = ingredientService.getAll();

        assertEquals(1, result.size());
        verify(ingredientRepository).findAll();
    }

    @Test
    @DisplayName("getById - Success")
    void getById_Success() {
        Ingredient ingredient = new Ingredient();
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.apply(ingredient)).thenReturn(new IngredientDto());

        IngredientDto result = ingredientService.getById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("getById - NotFound")
    void getById_NotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getById(1L));
    }

    @Test
    @DisplayName("create - Success")
    void create_Success() {
        IngredientDto dto = new IngredientDto();
        dto.setName("Sugar");
        Ingredient entity = new Ingredient();
        entity.setName("Sugar");

        when(ingredientRepository.existsByName("Sugar")).thenReturn(false);
        when(ingredientMapper.toEntity(dto)).thenReturn(entity);
        when(ingredientRepository.save(entity)).thenReturn(entity);
        when(ingredientMapper.apply(entity)).thenReturn(dto);

        IngredientDto result = ingredientService.create(dto);

        assertNotNull(result);
        verify(productService).invalidateCache();
        verify(ingredientRepository).save(any());
    }

    @Test
    @DisplayName("create - BadRequestException Name Exists")
    void create_NameExists() {
        IngredientDto dto = new IngredientDto();
        dto.setName("Sugar");

        when(ingredientRepository.existsByName("Sugar")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> ingredientService.create(dto));
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    @DisplayName("update - Success")
    void update_Success() {
        IngredientDto details = new IngredientDto();
        details.setName("New Name");
        Ingredient ingredient = new Ingredient();

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any())).thenReturn(ingredient);
        when(ingredientMapper.apply(any())).thenReturn(new IngredientDto());

        IngredientDto result = ingredientService.update(1L, details);

        assertNotNull(result);
        assertEquals("New Name", ingredient.getName());
        verify(productService).invalidateCache();
    }

    @Test
    @DisplayName("delete - Success with unlinking products")
    void delete_SuccessWithProducts() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Sugar");

        ingredient.setProducts(new java.util.HashSet<>());

        Product product = new Product();
        product.setId(100L);
        product.setIngredients(new java.util.HashSet<>());

        product.getIngredients().add(ingredient);
        ingredient.getProducts().add(product);

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        ingredientService.delete(1L);

        assertEquals(0, product.getIngredients().size());
        verify(ingredientRepository).delete(ingredient);
        verify(productService).invalidateCache();
    }

    @Test
    @DisplayName("delete - Success with NO products (Simple delete)")
    void delete_SuccessNoProducts() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setName("Salt");
        ingredient.setProducts(new HashSet<>());

        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient));

        ingredientService.delete(2L);

        verify(ingredientRepository).delete(ingredient);
        verify(productService).invalidateCache();
    }

    @Test
    @DisplayName("delete - NotFoundException")
    void delete_NotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.delete(1L));
    }
}