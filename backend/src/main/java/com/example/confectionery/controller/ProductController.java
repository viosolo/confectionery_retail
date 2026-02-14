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

/**
 * Контроллер для обработки API-запросов, связанных с кондитерскими изделиями.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  /**
   * Конструктор для внедрения зависимости сервиса продуктов.
   *
   * @param productService сервис для работы с бизнес-логикой продуктов.
   */
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Получает список всех доступных продуктов.
   *
   * @return список объектов ProductDTO.
   */
  @GetMapping
  public List<ProductDto> getProducts() {
    return productService.getAllProducts();
  }

  /**
   * Возвращает текстовое описание продукта по его имени.
   *
   * @param name название продукта.
   * @return строка с описанием товара.
   */
  @GetMapping("/description")
  public String getProductDescription(@RequestParam("name") String name) {
    ProductDto product = productService.getProductByName(name);
    return "Описание товара " + product.name() + ": " + product.description();
  }

  /**
   * Фильтрует продукты по их типу.
   *
   * @param type тип продукта (ZEPHYR, MACARON и т.д.).
   * @return список продуктов выбранного типа.
   */
  @GetMapping("/type/{type}")
  public List<ProductDto> getByType(@PathVariable ProductType type) {
    return productService.getProductsByType(type);
  }

  /**
   * Находит продукт по его уникальному идентификатору.
   *
   * @param id идентификатор продукта.
   * @return DTO продукта.
   */
  @GetMapping("/{id}")
  public ProductDto getProductById(@PathVariable Long id) {
    return productService.getProductById(id);
  }
}