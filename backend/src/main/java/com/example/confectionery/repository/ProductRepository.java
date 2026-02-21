package com.example.confectionery.repository;

import com.example.confectionery.entity.Nutrition;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.ProductType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

  private final List<Product> products = new ArrayList<>();
  private long idCounter = 1;

  public ProductRepository() {
    initData();
  }

  private void initData() {
    addProduct("Яблочный (Классика)", ProductType.ZEPHYR, "Яблоко", 2.6,
        new Nutrition(100, 310), 20, "Золотой стандарт зефира...");
    addProduct("Кофейный", ProductType.ZEPHYR, "Кофе", 2.7,
        new Nutrition(100, 330), 15, "Для тех, кто любит покрепче...");
    addProduct("Черничный", ProductType.ZEPHYR, "Черника", 3.0,
        new Nutrition(100, 305), 12, "Лесная ягода в каждом кусочке...");
    addProduct("Вишневый", ProductType.ZEPHYR, "Вишня", 2.9,
        new Nutrition(100, 315), 18, "Изысканная терпкость вишни...");
    addProduct("Клубничный", ProductType.ZEPHYR, "Клубника", 3.1,
        new Nutrition(100, 310), 25, "Классика летнего настроения...");
    addProduct("Апельсиновый", ProductType.ZEPHYR, "Апельсин", 2.8,
        new Nutrition(100, 320), 10, "Солнечный вкус...");

    addProduct("Зеленый лес", ProductType.MACARON, "Фисташка", 3.0,
        new Nutrition(30, 110), 50, "С крошкой ореха");
    addProduct("Лавандовое поле", ProductType.MACARON, "Лаванда", 3.10,
        new Nutrition(30, 105), 40, "Нежный аромат");
    addProduct("Соленая карамель", ProductType.MACARON, "Карамель", 2.80,
        new Nutrition(30, 120), 60, "Топ продаж");
    addProduct("Малиновый закат", ProductType.MACARON, "Малина", 3.30,
        new Nutrition(30, 100), 45, "С кислинкой");
    addProduct("Черничная ночь", ProductType.MACARON, "Черника", 3.10,
        new Nutrition(30, 105), 30, "Насыщенный цвет");

    addProduct("Классический эклер", ProductType.ECLAIR, "Ваниль", 4.0,
        new Nutrition(70, 250), 15, "С заварным кремом");
    addProduct("Шоколадный король", ProductType.ECLAIR, "Шоколад", 4.10,
        new Nutrition(85, 310), 10, "Двойной шоколад");
    addProduct("Тропический", ProductType.ECLAIR, "Манго", 3.90,
        new Nutrition(65, 220), 7, "С нежным муссом");
    addProduct("Ореховый", ProductType.ECLAIR, "Орех", 4.50,
        new Nutrition(90, 340), 5, "С фундуком");
    addProduct("Сливочная карамель", ProductType.ECLAIR, "Карамель", 4.20,
        new Nutrition(75, 290), 12, "С тягучей начинкой");
  }

  private void addProduct(String name, ProductType type, String flavor, Double price,
                          Nutrition nutrition, Integer stock, String description) {

    Product product = Product.builder()
        .id(idCounter++)
        .name(name)
        .type(type)
        .flavor(flavor)
        .price(price)
        .nutrition(nutrition)
        .stockQuantity(stock)
        .description(description)
        .build();

    products.add(product);
  }

  public List<Product> findAllProducts() {
    return products;
  }

  public Optional<Product> findProductById(Long id) {
    return products.stream()
        .filter(p -> p.getId().equals(id))
        .findFirst();
  }

  public Optional<Product> findByName(String name) {
    return products.stream()
        .filter(p -> p.getName().equalsIgnoreCase(name))
        .findFirst();
  }
}