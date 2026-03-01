package com.example.confectionery.repository;

import com.example.confectionery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Поиск пользователя по email (нужно для логина)
    Optional<User> findByEmail(String email);

    // Проверка существования email
    boolean existsByEmail(String email);
}
