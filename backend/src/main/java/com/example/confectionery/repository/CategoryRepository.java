package com.example.confectionery.repository;

import com.example.confectionery.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Не забудь импорт!

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Этот метод позволит искать категорию по точному имени
    // IgnoreCase добавит удобства: "Макаронс" и "макаронс" сработают одинаково
    Optional<Category> findByNameIgnoreCase(String name);
}