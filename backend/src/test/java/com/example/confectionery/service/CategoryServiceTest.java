package com.example.confectionery.service;

import com.example.confectionery.dto.CategoryRequest;
import com.example.confectionery.dto.CategoryResponse;
import com.example.confectionery.entity.Category;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.CategoryResponseMapper;
import com.example.confectionery.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryResponseMapper categoryResponseMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("getAllCategories - Success")
    void getAllCategories_ShouldReturnList() {
        Category category = new Category();
        CategoryResponse response = new CategoryResponse();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryResponseMapper.apply(any(Category.class))).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("saveCategory - Success")
    void saveCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setSlug("test-slug");
        request.setName("Test Name");

        Category savedCategory = new Category();
        savedCategory.setId(1L);

        when(categoryRepository.existsBySlug("test-slug")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryResponseMapper.apply(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse result = categoryService.saveCategory(request);

        assertNotNull(result);
        verify(productService).invalidateCache();
        verify(categoryRepository).save(any());
    }

    @Test
    @DisplayName("saveCategory - BadRequestException")
    void saveCategory_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setSlug("existing-slug");

        when(categoryRepository.existsBySlug("existing-slug")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.saveCategory(request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("getCategoryById - Success")
    void getCategoryById_Success() {
        Category category = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryResponseMapper.apply(category)).thenReturn(new CategoryResponse());

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("getCategoryById - NotFound")
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("getCategoryBySlug - Success")
    void getCategoryBySlug_Success() {
        Category category = new Category();
        when(categoryRepository.findBySlug("test")).thenReturn(Optional.of(category));
        when(categoryResponseMapper.apply(category)).thenReturn(new CategoryResponse());

        CategoryResponse result = categoryService.getCategoryBySlug("test");

        assertNotNull(result);
    }

    @Test
    @DisplayName("getCategoryBySlug - NotFound")
    void getCategoryBySlug_NotFound() {
        when(categoryRepository.findBySlug("none")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryBySlug("none"));
    }

    @Test
    @DisplayName("deleteCategory - Success")
    void deleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
        verify(productService).invalidateCache();
    }

    @Test
    @DisplayName("deleteCategory - NotFound")
    void deleteCategory_NotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
