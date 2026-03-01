package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.OrderMapper;
import com.example.confectionery.repository.OrderRepository;
import com.example.confectionery.repository.ProductRepository;
import com.example.confectionery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        // 1. Ищем пользователя
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + dto.getUserId() + " не найден"));

        // 2. Ищем продукты по списку ID
        // Исправлено: dto.getProductIds() вместо dto.productIds() (Lombok генерирует геттер)
        List<Product> products = productRepository.findAllById(dto.getProductIds());

        if (products.isEmpty()) {
            throw new BadRequestException("Нельзя создать заказ без продуктов");
        }

        // 3. Считаем сумму
        BigDecimal total = products.stream()
                .map(p -> BigDecimal.valueOf(p.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Собираем сущность Order через Builder
        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .products(products)
                .deliveryAddress(dto.getDeliveryAddress())
                .paymentMethod(dto.getPaymentMethod())
                .totalAmount(total)
                .status(Order.OrderStatus.PENDING)
                .build();

        // 5. Сохраняем и мапим в красивый ответ
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заказ  не найден");
        }
        orderRepository.deleteById(id);
    }
}
