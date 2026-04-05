package com.example.confectionery.controller;

import com.example.confectionery.dto.CategoryRequest;
import com.example.confectionery.dto.CategoryResponse;
import com.example.confectionery.dto.CategoryWithProduct;
import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.service.CategoryService;
import com.example.confectionery.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.saveCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/products/search")
    public ResponseEntity<CategoryWithProduct> getProductsByFilter(
            @RequestParam(name = "slug") String slug,
            @RequestParam(required = false) List<String> flavors,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {

        CategoryResponse category = categoryService.getCategoryBySlug(slug);

        Page<ProductResponse> productPage = productService.searchWithCache(slug, flavors, maxPrice, pageable);

        CategoryWithProduct response = new CategoryWithProduct(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                productPage
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}