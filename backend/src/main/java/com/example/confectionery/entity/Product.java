package com.example.confectionery.entity;

public class Product {
    private Long id;
    private String name;
    // 1. Добавляем поле с типом нашего Enum
    private ProductType type;
    private String description;
    private String flavor;
    private Double price;
    private Integer weight;
    private Integer calories;
    private Integer stockQuantity;

    public Product() {
    }


    public Product(Long id, String name, ProductType type, String description, String flavor,
                   Double price, Integer weight, Integer calories, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.type = type; // Присваиваем значение полю
        this.description = description;
        this.flavor = flavor;
        this.price = price;
        this.weight = weight;
        this.calories = calories;
        this.stockQuantity = stockQuantity;
    }


    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}