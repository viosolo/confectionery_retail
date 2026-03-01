package com.example.confectionery.repository;

import com.example.confectionery.entity.Category;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Добавь этот импорт

@Repository

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Nonnull
    List<Category> findAll();

    @EntityGraph(attributePaths = {"products", "products.ingredients"})
    @Query("SELECT c FROM Category c")
    List<Category> findAllOptimized();

    Optional<Category> findByNameIgnoreCase(String name);
}