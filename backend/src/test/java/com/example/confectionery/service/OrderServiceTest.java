package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import com.example.confectionery.entity.PaymentMethod;
import static org.mockito.ArgumentMatchers.argThat;
import com.example.confectionery.entity.Order;
import com.example.confectionery.entity.Product;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.OrderMapper;
import com.example.confectionery.repository.OrderRepository;
import com.example.confectionery.repository.ProductRepository;
import com.example.confectionery.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("getOrdersByUserId - Success")
    void getOrdersByUserId_Success() {
        Long userId = 1L;
        Order order = Order.builder()
                .id(1L)
                .user(User.builder().id(userId).email("violetta@mail.com").build())
                .deliveryAddress("Minsk")
                .totalAmount(BigDecimal.valueOf(100.0))
                .build();

        when(orderRepository.findAllByUserId(userId)).thenReturn(List.of(order));

        when(orderMapper.apply(order)).thenReturn(OrderResponseDto.builder()
                .id(1L)
                .userEmail("violetta@mail.com")
                .userName("Violetta")
                .totalAmount(BigDecimal.valueOf(100.0))
                .build());

        List<OrderResponseDto> result = orderService.getOrdersByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("violetta@mail.com", result.getFirst().getUserEmail());
        verify(orderRepository).findAllByUserId(userId);
        verify(orderMapper).apply(any(Order.class));
    }

    @Test
    @DisplayName("createOrderWithTransaction - Success (Authorized User)")
    void createOrder_User_Success() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);
        dto.setProductIds(List.of(1L, 1L, 2L));
        dto.setDeliveryAddress("Minsk");
        dto.setPaymentMethod(PaymentMethod.CASH);

        User user = User.builder().id(1L).firstName("Violetta").lastName("S").build();
        Product p1 = Product.builder().id(1L).price(10.0).stockQuantity(10).build();
        Product p2 = Product.builder().id(2L).price(5.0).stockQuantity(10).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(p1, p2));
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.apply(any(Order.class))).thenReturn(OrderResponseDto.builder().build());

        OrderResponseDto result = orderService.createOrderWithTransaction(dto);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(orderRepository).save(argThat(order ->
                order.getUser() != null &&
                        order.getTotalAmount().compareTo(BigDecimal.valueOf(25.0)) == 0 &&
                        order.getProducts().size() == 3
        ));
    }

    @Test
    @DisplayName("createOrderWithTransaction - Success (Guest Order)")
    void createOrder_Guest_Success() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(null);
        dto.setGuestName("Guest User");
        dto.setGuestPhone("+375291112233");
        dto.setProductIds(List.of(1L));
        dto.setDeliveryAddress("Minsk");
        dto.setPaymentMethod(PaymentMethod.CARD_ON_DELIVERY);

        Product p1 = Product.builder().id(1L).price(20.0).stockQuantity(5).build();

        when(productRepository.findAllById(anyList())).thenReturn(List.of(p1));
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.apply(any(Order.class))).thenReturn(OrderResponseDto.builder().build());

        OrderResponseDto result = orderService.createOrderWithTransaction(dto);

        assertNotNull(result);
        verify(userRepository, never()).findById(anyLong());
        verify(orderRepository).save(argThat(order ->
                order.getUser() == null &&
                        "Guest User".equals(order.getGuestName()) &&
                        order.getTotalAmount().compareTo(BigDecimal.valueOf(20.0)) == 0
        ));
    }

    @Test
    @DisplayName("createOrderWithTransaction - UserNotFoundException")
    void createOrder_UserNotFound() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrderWithTransaction(dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("createOrderWithTransaction - ProductNotFoundException")
    void createOrder_ProductNotFound() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);
        dto.setProductIds(List.of(1L, 2L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(new Product()));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrderWithTransaction(dto));
    }

    @Test
    @DisplayName("createOrderWithTransaction - OutOfStock")
    void createOrder_OutOfStock() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(null);
        dto.setProductIds(List.of(1L));

        Product product = Product.builder().id(1L).price(5.0).build();

        when(productRepository.findAllById(anyList())).thenReturn(List.of(product));
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(0);

        assertThrows(BadRequestException.class, () -> orderService.createOrderWithTransaction(dto));
    }

    @Test
    @DisplayName("getAllOrders - Success")
    void getAllOrders_Success() {
        Order order = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.apply(any())).thenReturn(new OrderResponseDto());

        List<OrderResponseDto> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("deleteOrder - Success")
    void deleteOrder_Success() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteOrder - NotFoundException")
    void deleteOrder_NotFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, never()).deleteById(anyLong());
    }
}