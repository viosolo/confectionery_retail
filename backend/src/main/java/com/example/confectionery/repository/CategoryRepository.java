package com.example.confectionery.repository;

import com.example.confectionery.entity.Category;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Nonnull
    List<Category> findAll();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);
}