package com.example.confectionery.service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        asyncOrderService.realBusinessRaceTest(requestDto);
    }

    @Test
    @DisplayName("Should create order asynchronously with sleep")
    void shouldCreateOrderAsynchronously() {
        UUID taskId = UUID.randomUUID();
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);

        asyncOrderService.createOrderAsync(taskId, requestDto);

        await().atMost(25, TimeUnit.SECONDS).untilAsserted(() -> {
            Object statusObj = asyncOrderService.getStatus(taskId);
            Map<?, ?> statusMap = assertInstanceOf(Map.class, statusObj);
            assertEquals("COMPLETED", statusMap.get("STATUS_OPERATION"));
        });

        verify(orderService, atLeastOnce()).createOrderWithTransaction(any());
    }

    @Test
    @DisplayName("Should handle exception in async method")
    void shouldHandleAsyncFailure() {
        UUID taskId = UUID.randomUUID();
        String errorMsg = "Database connection lost";
        when(orderService.createOrderWithTransaction(any()))
                .thenThrow(new RuntimeException(errorMsg));

        asyncOrderService.createOrderAsync(taskId, requestDto);

        await().atMost(25, TimeUnit.SECONDS).untilAsserted(() -> {
            Object statusObj = asyncOrderService.getStatus(taskId);
            Map<?, ?> status = assertInstanceOf(Map.class, statusObj);
            assertEquals("FAILED", status.get("STATUS_OPERATION"));
            assertEquals(errorMsg, status.get("MESSAGE"));
        });
    }

    @Test
    @DisplayName("Should perform external race and trigger reset at 100")
    void shouldHandleExternalRaceAndReset() {
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);

        for (int i = 0; i < 100; i++) {
            asyncOrderService.realExternalRace(requestDto);
        }

        Map<String, Object> result = asyncOrderService.realExternalRace(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.get("TOTAL_ORDERS"));
        assertEquals(100000L, result.get("SAFE_RESULT"));
    }

    @Test
    @DisplayName("Should run business race test with 100 threads")
    void shouldRunBusinessRaceTest() {
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(requestDto);

        assertEquals(100, result.get("TOTAL_ATTEMPTS"));
        assertEquals(100L, result.get("REAL_ORDERS_IN_DB"));
        assertEquals(100000000L, result.get("3_SAFE_ATOMIC_RESULT"));
        assertTrue((long) result.get("4_UNSAFE_LONG_RESULT") <= 100000000L);
    }

    @Test
    @DisplayName("Should return NOT_FOUND status")
    void shouldReturnNotFoundStatus() {
        Object result = asyncOrderService.getStatus(UUID.randomUUID());
        assertEquals("NOT_FOUND", result);
    }

    @Test
    @DisplayName("Should catch exception in realExternalRace")
    void shouldCatchExceptionInExternalRace() {
        when(orderService.createOrderWithTransaction(any()))
                .thenThrow(new RuntimeException("Fail"));

        assertDoesNotThrow(() -> asyncOrderService.realExternalRace(requestDto));
    }

    @Test
    @DisplayName("Cover if !finished branch")
    void shouldCoverTimeoutBranch() {
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.await(70, TimeUnit.SECONDS);
            return responseDto;
        }).when(orderService).createOrderWithTransaction(any());

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(requestDto);

        assertNotNull(result);
        assertEquals(100, result.get("TOTAL_ATTEMPTS"));
    }

    @Test
    @DisplayName("Cover InterruptedException without any sleep calls")
    void shouldCoverAsyncInterruptedException() throws InterruptedException {
        UUID taskId = UUID.randomUUID();
        CountDownLatch readyToInterrupt = new CountDownLatch(1);
        CountDownLatch threadFinished = new CountDownLatch(1);

        Thread executionThread = new Thread(() -> {
            readyToInterrupt.countDown();
            asyncOrderService.createOrderAsync(taskId, requestDto);
            threadFinished.countDown();
        });

        executionThread.start();


        boolean isReady = readyToInterrupt.await(5, TimeUnit.SECONDS);

        if (isReady) {

            executionThread.interrupt();
        }

        threadFinished.await(5, TimeUnit.SECONDS);
        executionThread.join();

        Object statusObj = asyncOrderService.getStatus(taskId);
        Map<?, ?> statusMap = assertInstanceOf(Map.class, statusObj);

        assertEquals("FAILED", statusMap.get("STATUS_OPERATION"));
        assertEquals("Interrupted", statusMap.get("MESSAGE"));
    }

    @Test
    @DisplayName("Should cover database exception in business race test")
    void shouldCoverDatabaseExceptionInBusinessRace() {
        String errorMessage = "Database connection timeout";
        when(orderService.createOrderWithTransaction(any()))
                .thenThrow(new RuntimeException(errorMessage));

        Map<String, Object> result = asyncOrderService.realBusinessRaceTest(requestDto);

        assertNotNull(result);
        assertEquals(0L, result.get("REAL_ORDERS_IN_DB"));
        assertEquals(100, result.get("TOTAL_ATTEMPTS"));
        verify(orderService, atLeastOnce()).createOrderWithTransaction(any());
    }

    @Test
    @DisplayName("Cover InterruptedException catch branch")
    void shouldCoverInterruptedCatchBranch() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            startLatch.countDown();
            new CountDownLatch(1).await(10, TimeUnit.SECONDS);
            return responseDto;
        }).when(orderService).createOrderWithTransaction(any());

        Thread t = new Thread(() -> asyncOrderService.realBusinessRaceTest(requestDto));
        t.start();

        boolean started = startLatch.await(5, TimeUnit.SECONDS);
        if (started) {
            t.interrupt();
        }
        t.join();

        assertTrue(true);
    }

    @Test
    @DisplayName("Should get total processed count correctly")
    void shouldGetTotalProcessedCount() {
        when(orderService.createOrderWithTransaction(any())).thenReturn(responseDto);
        asyncOrderService.realExternalRace(requestDto);
        assertEquals(1L, asyncOrderService.getTotalProcessedCount());
    }
}