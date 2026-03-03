package com.example.confectionery.service;

import com.example.confectionery.entity.Category;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllCategoriesOptimized() {
        return categoryRepository.findAllOptimized();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория с ID " + id + " не найдена"));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категория " + id + " не найдена");
        }
        categoryRepository.deleteById(id);
    }
}
