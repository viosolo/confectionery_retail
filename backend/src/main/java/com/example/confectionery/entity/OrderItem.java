package com.example.confectionery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false)
  private String productName; // Название сладости (например, "Торт Наполеон")

  @Column(nullable = false)
  private Integer quantity;    // Количество (например, 2 шт)

  @Column(nullable = false)
  private BigDecimal price;    // Цена за 1 шт на момент покупки
}