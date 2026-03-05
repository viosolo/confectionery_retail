package com.example.confectionery.repository;

import com.example.confectionery.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull; // Используем стандартный Spring импорт
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @NonNull
    List<Product> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category", "ingredients"})
    Optional<Product> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"category", "ingredients"})
    Optional<Product> findByName(String name);

    @EntityGraph(attributePaths = {"category", "ingredients"})
    List<Product> findAllByActiveTrue();

    @EntityGraph(attributePaths = {"category", "ingredients"})
    List<Product> findByCategoryId(Long categoryId);
}