package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.exception.TransactionDemoException;
import com.example.confectionery.mapper.OrderMapper;
import com.example.confectionery.repository.OrderRepository;
import com.example.confectionery.repository.ProductRepository;
import com.example.confectionery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderResponseDto createOrderWithoutTransaction(OrderRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        Order order = Order.builder()
                .orderNumber("ERR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .deliveryAddress(dto.getDeliveryAddress())
                .totalAmount(BigDecimal.ZERO)
                .notes("БЕЗ ТРАНЗАКЦИИ")
                .status(Order.OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);
        log.info(">>> Шаг 1: Заказ сохранен в БД (БЕЗ ТРАНЗАКЦИИ)");

        processProductsWithBug(dto.getProductIds());

        return orderMapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDto createOrderWithTransaction(OrderRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<Product> products = productRepository.findAllById(dto.getProductIds());
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Не выбрано ни одного существующего товара!");
        }

        boolean hasInactive = products.stream().anyMatch(p -> !p.isActive());
        if (hasInactive) {
            throw new ResourceNotFoundException("Один из выбранных товаров больше недоступен для заказа (архивирован)!");
        }

        double total = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        Order order = Order.builder()
                .orderNumber("OK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .products(products)
                .totalAmount(BigDecimal.valueOf(total))
                .deliveryAddress(dto.getDeliveryAddress())
                .paymentMethod(dto.getPaymentMethod())
                .notes(dto.getNotes())
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        log.info(">>> Заказ {} успешно сохранен на сумму {}", savedOrder.getOrderNumber(), total);

        return orderMapper.toResponseDTO(savedOrder);
    }


    private void processProductsWithBug(List<Long> productIds) {
        for (Long productId : productIds) {

            if (productId == 0) {
                throw new TransactionDemoException("Критическая ошибка! ID=0 спровоцировал откат.");
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден: " + productId));

            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);
            log.info(">>> Шаг 2: Списан товар ID: " + productId);
        }
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заказ  не найден");
        }
        orderRepository.deleteById(id);
    }
}
