package com.example.confectionery.controller;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.ProductType;
import com.example.confectionery.service.ProductService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/description")
    public String getProductDescription(@RequestParam("name") String name) {
        ProductDto product = productService.getProductByName(name);
        return "Описание товара " + product.name() + ": " + product.description();
    }

    @GetMapping("/type/{type}")
    public List<ProductDto> getByType(@PathVariable ProductType type) {
        return productService.getProductsByType(type);
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}