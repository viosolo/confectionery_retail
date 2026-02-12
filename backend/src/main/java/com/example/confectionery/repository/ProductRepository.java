package com.example.confectionery.repository;

import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.ProductType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();


    public ProductRepository() {
        // --- ЗЕФИР ---
        products.add(new Product(1L, "Яблочный (Классика)", ProductType.ZEPHYR, "Золотой стандарт зефира...", "Яблоко", 2.6, 100, 310, 20));
        products.add(new Product(2L, "Кофейный", ProductType.ZEPHYR, "Для тех, кто любит покрепче...", "Кофе", 2.7, 100, 330, 15));
        products.add(new Product(3L, "Черничный", ProductType.ZEPHYR, "Лесная ягода в каждом кусочке...", "Черника", 3.0, 100, 305, 12));
        products.add(new Product(4L, "Вишневый", ProductType.ZEPHYR, "Изысканная терпкость вишни...", "Вишня", 2.9, 100, 315, 18));
        products.add(new Product(5L, "Клубничный", ProductType.ZEPHYR, "Классика летнего настроения...", "Клубника", 3.1, 100, 310, 25));
        products.add(new Product(6L, "Апельсиновый", ProductType.ZEPHYR, "Солнечный вкус...", "Апельсин", 2.8, 100, 320, 10));
        // ---МАКАРОНС ---
        products.add(new Product(7L, "Зеленый лес", ProductType.MACARON, "С крошкой ореха", "Фисташка", 90.0, 30, 110, 50));
        products.add(new Product(8L, "Лавандовое поле", ProductType.MACARON, "Нежный аромат", "Лаванда", 95.0, 30, 105, 40));
        products.add(new Product(9L, "Соленая карамель", ProductType.MACARON, "Топ продаж", "Карамель", 100.0, 30, 120, 60));
        products.add(new Product(10L, "Малиновый закат", ProductType.MACARON, "С кислинкой", "Малина", 90.0, 30, 100, 45));
        products.add(new Product(11L, "Черничная ночь", ProductType.MACARON, "Насыщенный цвет", "Черника", 95.0, 30, 105, 30));

        // --- ЭКЛЕРЫ ---
        products.add(new Product(12L, "Классический эклер", ProductType.ECLAIR, "С заварным кремом", "Ваниль", 130.0, 70, 250, 15));
        products.add(new Product(13L, "Шоколадный король", ProductType.ECLAIR, "Двойной шоколад", "Шоколад", 145.0, 85, 310, 10));
        products.add(new Product(14L, "Тропический", ProductType.ECLAIR, "С нежным муссом", "Манго", 160.0, 65, 220, 7));
        products.add(new Product(15L, "Ореховый", ProductType.ECLAIR, "С фундуком", "Орех", 155.0, 90, 340, 5));
        products.add(new Product(16L, "Сливочная карамель", ProductType.ECLAIR, "С тягучей начинкой", "Карамель", 150.0, 75, 290, 12));
    }


    public List<Product> findAllProducts() {

        return products;
    }
    public Optional<Product> findProductById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst(); // Это вернет Optional<Product>
    }

    public Optional<Product> findByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}