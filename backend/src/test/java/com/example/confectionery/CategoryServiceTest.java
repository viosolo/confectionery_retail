package com.example.confectionery; // Проверь, чтобы путь совпадал с папкой

import com.example.confectionery.entity.Category;
import com.example.confectionery.repository.CategoryRepository;
import com.example.confectionery.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void saveCategory_ShouldReturnSavedCategory() {
        // Данные
        Category category = Category.builder().name("Торты").slug("torty").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Действие
        Category saved = categoryService.saveCategory(category);

        // Проверка
        assertNotNull(saved);
        assertEquals("Торты", saved.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        Category category = Category.builder().id(1L).name("Торты").build();
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        Category found = categoryService.getCategoryById(1L);

        assertEquals("Торты", found.getName());
    }

    @Test
    void deleteCategory_ShouldInvokeRepository() {
        Long id = 1L;
        // Если в сервисе есть проверка existsById, нужно её тоже замокать:
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteCategory(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }
}
