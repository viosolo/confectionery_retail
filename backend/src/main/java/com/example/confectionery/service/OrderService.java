package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.OrderStatus;
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
                .deliveryAddress(dto.getDeliveryAddress())
                .totalAmount(BigDecimal.ZERO)
                .notes("БЕЗ ТРАНЗАКЦИИ")
                .paymentMethod(dto.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .build();

        // СРАЗУ СОХРАНЯЕМ ЗАКАЗ. Он уже в базе, даже если дальше всё упадет.
        order = orderRepository.save(order);
        log.info(">>> Шаг 1: Заказ {} сохранен в БД", order.getOrderNumber());

        // Цикл списания товаров. Если на втором товаре вылетит ResourceNotFoundException...
        for (Long productId : dto.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ошибка! Товар ID " + productId + " не найден. Цикл прерван."));

            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);
            log.info(">>> Шаг 2: Списан товар ID: {}", productId);
        }

        return orderMapper.toResponseDTO(order);
    }

    @Transactional
    public OrderResponseDto createOrderWithTransaction(OrderRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // Сначала находим все товары
        List<Product> products = productRepository.findAllById(dto.getProductIds());

        // Если какого-то товара из списка нет в БД — выбрасываем исключение
        if (products.size() != dto.getProductIds().size()) {
            throw new ResourceNotFoundException("Один или несколько товаров не найдены в базе!");
        }

        BigDecimal total = products.stream()
                .map(p -> BigDecimal.valueOf(p.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Списываем остатки
        for (Product product : products) {
            if (product.getStockQuantity() < 1) {
                throw new IllegalStateException("Товара " + product.getName() + " нет в наличии");
            }
            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);
        }

        Order order = Order.builder()
                .orderNumber("OK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .products(products)
                .totalAmount(total)
                .deliveryAddress(dto.getDeliveryAddress())
                .paymentMethod(dto.getPaymentMethod())
                .notes(dto.getNotes())
                .status(OrderStatus.PENDING)
                .build();

        // Все изменения (и товары, и заказ) запишутся в БД ОДНИМ махом в конце метода.
        Order savedOrder = orderRepository.save(order);
        log.info(">>> Заказ {} успешно сохранен в рамках транзакции", savedOrder.getOrderNumber());

        return orderMapper.toResponseDTO(savedOrder);
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
