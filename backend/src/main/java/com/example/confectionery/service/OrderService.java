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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId)
                .stream()
                .map(orderMapper)
                .toList();
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ORDER_NOT_FOUND, id)
                ));

        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponseDto createOrderWithTransaction(OrderRequestDto dto) {
        log.info(">>> Attempting to create order. User ID: {}, Guest Name: {}", dto.getUserId(), dto.getGuestName());

        User user = (dto.getUserId() != null)
                ? userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error(">>> Order creation failed: User ID {} not found", dto.getUserId());
                    return new ResourceNotFoundException(String.format(USER_NOT_FOUND, dto.getUserId()));
                })
                : null;

        List<Long> uniqueProductIds = dto.getProductIds().stream().distinct().toList();
        List<Product> productsInDb = productRepository.findAllById(uniqueProductIds);

        if (productsInDb.size() != uniqueProductIds.size()) {
            log.error(">>> Order creation failed: Some products from list {} are missing in DB", uniqueProductIds);
            throw new ResourceNotFoundException(PRODUCTS_NOT_FOUND);
        }

        Map<Long, Product> productMap = productsInDb.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        BigDecimal total = BigDecimal.ZERO;
        List<Product> orderProducts = new ArrayList<>();

        for (Long productId : dto.getProductIds()) {
            Product product = productMap.get(productId);

            int updatedRows = productRepository.decreaseStock(productId, 1);
            if (updatedRows == 0) {
                log.warn(">>> Order rejected: Product ID {} is out of stock", productId);
                throw new BadRequestException(String.format(OUT_OF_STOCK, "Product ID: " + productId));
            }

            total = total.add(BigDecimal.valueOf(product.getPrice()));
            orderProducts.add(product);
        }


        Order order = Order.builder()
                .orderNumber("OK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .guestName(user == null ? dto.getGuestName() : null)
                .guestPhone(user == null ? dto.getGuestPhone() : null)
                .products(orderProducts)
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

    @Transactional(readOnly = true)
    public List<OrderResponseDto> searchOrders(String query) {
        List<Order> orders;

        if (query == null || query.isBlank()) {
            orders = orderRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
        } else {
            orders = orderRepository.findAllByOrderNumberContainingIgnoreCaseOrGuestNameContainingIgnoreCaseOrUserFirstNameContainingIgnoreCase(
                    query, query, query
            );
        }

        return orders.stream()
                .map(orderMapper)
                .toList();
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
