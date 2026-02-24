package com.example.confectionery.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String orderNumber;

  // Внутри класса Order
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;// ID пользователя, который сделал заказ

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private String userEmail;

  // Тот самый список продуктов в заказе
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false)
  private BigDecimal totalAmount; // Итоговая сумма

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.PENDING;

  private String deliveryAddress; // Упростили с ShippingAddress до строки для начала
  private String paymentMethod;
  private String notes; // Заметки (например, "не звонить в звонок")

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
  }
}