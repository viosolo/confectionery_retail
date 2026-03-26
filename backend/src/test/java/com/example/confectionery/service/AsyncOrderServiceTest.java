package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncOrderServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AsyncOrderService asyncOrderService;

    private OrderRequestDto requestDto;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequestDto();
        responseDto = new OrderResponseDto();
    }

    @Test
    @DisplayName("Successful asynchronous order creation")
    void createOrderAsync_Success() {
        UUID taskId = UUID.randomUUID();
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);

        asyncOrderService.createOrderAsync(taskId, requestDto);

        Object status = asyncOrderService.getStatus(taskId);
        Map<?, ?> statusMap = assertInstanceOf(Map.class, status);
        assertEquals("COMPLETED", statusMap.get("STATUS_OPERATION"));
        assertEquals(1, asyncOrderService.getTotalProcessedCount());
    }

    @Test
    @DisplayName("Verification of data loss during race condition")
    void shouldShowDataLoss() {
        long expectedMax = 100 * 1000000L;

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(new OrderRequestDto());

        long safe = ((Number) result.get("3_SAFE_ATOMIC_RESULT")).longValue();
        long unsafe = ((Number) result.get("4_UNSAFE_LONG_RESULT")).longValue();

        assertEquals(expectedMax, safe);
        assertTrue(unsafe < safe);
    }

    @Test
    @DisplayName("Asynchronous order creation failure handling")
    void createOrderAsync_Exception() {
        UUID taskId = UUID.randomUUID();
        when(orderService.createOrderWithTransaction(any())).thenThrow(new RuntimeException("DB Error"));

        asyncOrderService.createOrderAsync(taskId, requestDto);

        Object status = asyncOrderService.getStatus(taskId);
        Map<?, ?> statusMap = assertInstanceOf(Map.class, status);
        assertEquals("FAILED", statusMap.get("STATUS_OPERATION"));
        assertEquals("DB Error", statusMap.get("MESSAGE"));
    }

    @Test
    @DisplayName("Check NOT_FOUND status for non-existent task")
    void getStatus_NotFound() {
        assertEquals("NOT_FOUND", asyncOrderService.getStatus(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Race condition and business logic execution test")
    void realBusinessRaceTest_Execution() {
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(requestDto);

        assertEquals(100, (int) result.get("TOTAL_ATTEMPTS"));
        assertEquals(100L, ((Number) result.get("REAL_ORDERS_IN_DB")).longValue());
        verify(orderService, times(100)).createOrderWithTransaction(any());
    }

    @Test
    @DisplayName("Error handling during Race Test")
    void realBusinessRaceTest_WithErrors() {
        when(orderService.createOrderWithTransaction(any())).thenThrow(new RuntimeException("Error"));

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(requestDto);

        long realOrders = ((Number) result.get("REAL_ORDERS_IN_DB")).longValue();

        assertEquals(0L, realOrders);
        verify(orderService, times(100)).createOrderWithTransaction(any());
    }

    @Test
    @DisplayName("createOrderAsync - Handle InterruptedException coverage")
    void createOrderAsync_Interrupted() {
        UUID taskId = UUID.randomUUID();


        Thread.currentThread().interrupt();

        asyncOrderService.createOrderAsync(taskId, requestDto);

        Object status = asyncOrderService.getStatus(taskId);
        Map<?, ?> statusMap = assertInstanceOf(Map.class, status);

        assertEquals("FAILED", statusMap.get("STATUS_OPERATION"));
        assertEquals("Interrupted", statusMap.get("MESSAGE"));

        Thread.interrupted();
    }

    @Test
    @DisplayName("Coverage: Handle awaitTermination timeout")
    void realBusinessRaceTest_Timeout() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);

        try (var mockedExecutors = mockStatic(Executors.class)) {
            mockedExecutors.when(() -> Executors.newFixedThreadPool(anyInt()))
                    .thenReturn(mockExecutor);

            when(mockExecutor.awaitTermination(anyLong(), any(TimeUnit.class)))
                    .thenReturn(false);

            asyncOrderService.realBusinessRaceTest(new OrderRequestDto());

            verify(mockExecutor).shutdown();
        }
    }

    @Test
    @DisplayName("Coverage: Handle InterruptedException")
    void realBusinessRaceTest_Interrupted() throws InterruptedException {
        ExecutorService mockExecutor = mock(ExecutorService.class);

        try (var mockedExecutors = mockStatic(Executors.class)) {
            mockedExecutors.when(() -> Executors.newFixedThreadPool(anyInt()))
                    .thenReturn(mockExecutor);

            when(mockExecutor.awaitTermination(anyLong(), any(TimeUnit.class)))
                    .thenThrow(new InterruptedException("Test"));

            asyncOrderService.realBusinessRaceTest(new OrderRequestDto());

            assertTrue(Thread.interrupted());
        }
    }
}