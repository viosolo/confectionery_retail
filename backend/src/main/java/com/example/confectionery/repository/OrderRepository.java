package com.example.confectionery.repository;

import com.example.confectionery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Позволит получить историю заказов юзера
    List<Order> findByUserId(Long userId);
}