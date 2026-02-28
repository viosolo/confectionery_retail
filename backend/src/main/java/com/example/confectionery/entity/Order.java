package com.example.confectionery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;

// Lombok для сокращения кода (геттеры, сеттеры, конструкторы)
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


// Аннотации Spring Data для автоматического отслеживания даты создания/изменения
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// Стандартные Java-типы
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class) // Чтобы работали @CreatedDate и @LastModifiedDate
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber; // Уникальный номер заказа (например, ORD-12345)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Ссылка на пользователя

    @Column(nullable = false)
    private String userName; // Имя на момент заказа (снапшот)

    @Column(nullable = false)
    private String userEmail; // Email на момент заказа

    // Связь с позициями заказа
    // cascade = ALL значит, что при сохранении заказа сохранятся и все его позиции
    // orphanRemoval = true значит, что если удалить позицию из списка, она удалится и из базы
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount; // Итоговая сумма заказа

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    private String deliveryAddress;
    private String paymentMethod;
    private String notes;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Важный метод-помощник для синхронизации двусторонней связи.
     * Когда мы добавляем товар в заказ, товар должен "узнать", к какому заказу он относится.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Метод для удобного удаления позиции
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public enum OrderStatus {
        PENDING,    // Ожидает обработки
        CONFIRMED,  // Подтвержден
        PROCESSING, // Готовится
        SHIPPED,    // В пути
        DELIVERED,  // Доставлен
        CANCELLED,  // Отменен
        REFUNDED    // Оформлен возврат
    }
}