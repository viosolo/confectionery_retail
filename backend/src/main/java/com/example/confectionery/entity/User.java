package com.example.confectionery.entity;

// Явные импорты из Jakarta Persistence
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Авто-инкремент ID (1, 2, 3...)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(unique = true, nullable = false)
  private String phone;
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public enum Role {
    USER, ADMIN
  }
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Order> tasks = new ArrayList<>();

}