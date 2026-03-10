package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.OrderStatus;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
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

    private static final String USER_NOT_FOUND = "Пользователь с ID %d не найден";
    private static final String ORDER_NOT_FOUND = "Заказ с ID %d не найден";
    private static final String PRODUCTS_NOT_FOUND = "Один или несколько товаров не найдены в базе!";
    private static final String OUT_OF_STOCK = "Товара %s нет в наличии";

    @Transactional
    public OrderResponseDto createOrderWithTransaction(OrderRequestDto dto) {
        log.info(">>> Attempting to create order for user ID: {}", dto.getUserId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error(">>> Order creation failed: User ID {} not found", dto.getUserId());
                    return new ResourceNotFoundException(String.format(USER_NOT_FOUND, dto.getUserId()));
                });

        List<Product> products = productRepository.findAllById(dto.getProductIds());
        if (products.size() != dto.getProductIds().size()) {

            log.error(">>> Order creation failed: Some products from list {} are missing in DB", dto.getProductIds());

            throw new ResourceNotFoundException(PRODUCTS_NOT_FOUND);
        }

        BigDecimal total = products.stream()
                .map(p -> BigDecimal.valueOf(p.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Product product : products) {
            if (product.getStockQuantity() < 1) {

                log.warn(">>> Order rejected: Product '{}' is out of stock", product.getName());

                throw new BadRequestException(String.format(OUT_OF_STOCK, product.getName()));
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

        Order savedOrder = orderRepository.save(order);
        log.info(">>> Order {} successfully saved. Total amount: {}", savedOrder.getOrderNumber(), total);

        return orderMapper.apply(savedOrder);
    }

    public List<OrderResponseDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        log.info(">>> Requesting all orders list. Found {} orders", orders.size());

        return orderRepository.findAll().stream()
                .map(orderMapper)
                .toList();
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {

            log.error(">>> Deletion failed: Order with ID {} not found", id);
            throw new ResourceNotFoundException(String.format(ORDER_NOT_FOUND, id));
        }
        orderRepository.deleteById(id);
        log.info(">>> Order with ID {} successfully deleted", id);
    }
}
