package com.example.confectionery.service;

import com.example.confectionery.dto.CategoryRequest;
import com.example.confectionery.dto.CategoryResponse;
import com.example.confectionery.entity.Category;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.CategoryResponseMapper;
import com.example.confectionery.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryResponseMapper categoryResponseMapper;

    private final ProductService productService;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryResponseMapper)
                .toList();
    }

    @Transactional
    public CategoryResponse saveCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        productService.invalidateCache();
        return categoryResponseMapper.apply(saved);
    }

    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryResponseMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Категория с ID " + id + " не найдена"));
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категория " + id + " не найдена");
        }
        categoryRepository.deleteById(id);
        productService.invalidateCache();
    }
}