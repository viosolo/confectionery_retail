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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.times;
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
    @DisplayName("createOrderWithTransaction - Success")
    void createOrder_Success() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);
        dto.setProductIds(List.of(1L, 2L));

        User user = new User();
        user.setId(1L);

        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Cake");
        p1.setPrice(10.0);
        p1.setStockQuantity(5);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Cookie");
        p2.setPrice(5.0);
        p2.setStockQuantity(10);

        Order savedOrder = new Order();
        savedOrder.setOrderNumber("OK-12345678");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(p1, p2));
        when(productRepository.decreaseStock(anyLong(), anyInt())).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.apply(any(Order.class))).thenReturn(new OrderResponseDto());

        OrderResponseDto result = orderService.createOrderWithTransaction(dto);

        assertNotNull(result);
        verify(productRepository, times(2)).decreaseStock(anyLong(), anyInt());
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
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
    @DisplayName("createOrderWithTransaction - BadRequestException OutOfStock")
    void createOrder_OutOfStock() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);
        dto.setProductIds(List.of(1L));

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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