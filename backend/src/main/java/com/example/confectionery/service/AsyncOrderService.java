package com.example.confectionery.service;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncOrderService {

    private long unsafeCount = 0;
    private final AtomicLong safeCount = new AtomicLong(0);

    private static final String STATUS_KEY = "STATUS_OPERATION";
    private final OrderService orderService;
    private final Map<UUID, Object> taskStatuses = new ConcurrentHashMap<>();
    private final AtomicLong totalProcessedOrders = new AtomicLong(0);

    @Async
    public void createOrderAsync(UUID taskId, OrderRequestDto dto) {

        taskStatuses.put(taskId, Map.of(STATUS_KEY, "PROCESSING"));

        try {
            Thread.sleep(20000);

            OrderResponseDto result = orderService.createOrderWithTransaction(dto);

            totalProcessedOrders.incrementAndGet();

            taskStatuses.put(taskId, Map.of(
                    STATUS_KEY, "COMPLETED",
                    "DATA", result
            ));

        } catch (InterruptedException e) {
            taskStatuses.put(taskId, Map.of(STATUS_KEY, "FAILED", "MESSAGE", "Interrupted"));
            Thread.currentThread().interrupt();
        } catch (Exception e) {

            taskStatuses.put(taskId, Map.of(
                    STATUS_KEY, "FAILED",
                    "MESSAGE", e.getMessage()
            ));
            log.error("Async order failed: {}", e.getMessage());
        }
    }

    private void resetCounters() {
        this.unsafeCount = 0;
        this.safeCount.set(0);
        this.totalProcessedOrders.set(0);
    }

    private void performRaceCondition(int iterations) {
        for (int i = 0; i < iterations; i++) {
            unsafeCount++;
            safeCount.incrementAndGet();
        }
    }

    public Object getStatus(UUID taskId) {
        return taskStatuses.getOrDefault(taskId, "NOT_FOUND");
    }

    public Map<String, Object> realBusinessRaceTest(OrderRequestDto dto) {
        resetCounters();

        int threadCount = 100;
        int iterationsPerThread = 1000000;

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        orderService.createOrderWithTransaction(dto);
                        totalProcessedOrders.incrementAndGet();
                    } catch (Exception e) {
                        log.error("Error in database: {}", e.getMessage());
                    }

                    performRaceCondition(iterationsPerThread);
                });
            }

            executor.shutdown();
            boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);

            if (!finished) {
                log.warn("Threads did not finish in time");
            }

        } catch (InterruptedException e) {
            log.error("Test interrupted");
            Thread.currentThread().interrupt();
        }

        return Map.of(
                "SAFE_ATOMIC_RESULT", safeCount.get(),
                "UNSAFE_LONG_RESULT", unsafeCount
        );
    }


    public long getTotalProcessedCount() {
        return totalProcessedOrders.get();
    }

}