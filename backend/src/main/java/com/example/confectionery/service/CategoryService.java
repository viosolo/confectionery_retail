package com.example.confectionery.service;

import com.example.confectionery.dto.CategoryRequest;
import com.example.confectionery.dto.CategoryResponse;
import com.example.confectionery.entity.Category;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.CategoryResponseMapper;
import com.example.confectionery.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryResponseMapper categoryResponseMapper;

    private final ProductService productService;

    private static final String CATEGORY_NOT_FOUND_ID = "Категория с ID %d не найдена";
    private static final String CATEGORY_NOT_FOUND_SLUG = "Категория со слагом %s не найдена";

    public List<CategoryResponse> getAllCategories() {
        log.info(">>> Requesting all categories list");
        return categoryRepository.findAll()
                .stream()
                .map(categoryResponseMapper)
                .toList();
    }

    @Transactional
    public CategoryResponse saveCategory(CategoryRequest request) {
        log.info(">>> Attempting to save new category with slug: {}", request.getSlug());

        if (categoryRepository.existsBySlug(request.getSlug())) {
            log.warn(">>> Category save failed: slug '{}' already exists", request.getSlug());
            throw new BadRequestException("Категория со слагом " + request.getSlug() + " уже существует");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        Category saved = categoryRepository.save(category);
        log.info(">>> Category successfully saved with ID: {}", saved.getId());
        productService.invalidateCache();
        return categoryResponseMapper.apply(saved);
    }

    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryResponseMapper)
                .orElseThrow(() -> {

                    log.error(">>> Category with ID {} not found", id);
                    return new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND_ID, id));
                });
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryResponseMapper)
                .orElseThrow(() -> {
                    log.error(">>> Category with slug '{}' not found", slug);
                    return new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND_SLUG, slug));
                });
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info(">>> Attempting to delete category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.error(">>> Deletion failed: Category ID {} not found", id);
            throw new ResourceNotFoundException(String.format(CATEGORY_NOT_FOUND_ID, id));
        }
        categoryRepository.deleteById(id);
        log.info(">>> Category with ID {} successfully deleted", id);

        productService.invalidateCache();
    }
}