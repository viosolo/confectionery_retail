package com.example.confectionery.controller;

import com.example.confectionery.dto.ProductDto;
import com.example.confectionery.entity.Product;
import com.example.confectionery.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type) {

        if (name != null && !name.isEmpty()) {
            return List.of(productService.getProductByName(name));
        }

        if (type != null && !type.isEmpty()) {
            return productService.getProductsByCategory(type);
        }

        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> patch(@PathVariable Long id, @RequestBody ProductDto updatesDto) {
        // 1. Принимаем ProductDto (в нем могут быть заполнены только цена или только имя)
        // 2. Передаем этот DTO в сервис
        ProductDto updatedProduct = productService.patchProduct(id, updatesDto);

        // 3. Возвращаем обновленный результат со статусом 200 OK
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}