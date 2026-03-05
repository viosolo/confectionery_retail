package com.example.confectionery.repository;

import com.example.confectionery.entity.Category;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Nonnull
    List<Category> findAll();

}