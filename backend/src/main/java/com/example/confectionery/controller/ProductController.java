package com.example.confectionery.controller;
import com.example.confectionery.dto.ProductDTO; // ОБЯЗАТЕЛЬНО добавить
import com.example.confectionery.entity.ProductType;
import com.example.confectionery.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. Получаем все (возвращаем List<ProductDTO>)
    @GetMapping
    public List<ProductDTO> getProducts() {
        return productService.getAllProducts(); // Исправили название метода
    }

    // 2. Твой метод с @RequestParam
    @GetMapping("/description")
    public String getProductDescription(@RequestParam("name") String name) {
        ProductDTO product = productService.getProductByName(name);
        return "Описание товара " + product.name() + ": " + product.description();
    }

    // 3. Фильтр по типу через PathVariable (возвращаем List<ProductDTO>)
    @GetMapping("/type/{type}") // Убрали лишнее /products/ из пути, так как базовый путь уже /api/products
    public List<ProductDTO> getByType(@PathVariable ProductType type) {
        return productService.getProductsByType(type);
    }

    // 4. Получение по ID (возвращаем ProductDTO)
    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}