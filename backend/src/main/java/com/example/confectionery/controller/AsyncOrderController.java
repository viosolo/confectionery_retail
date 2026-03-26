package com.example.confectionery.controller;

import com.example.confectionery.dto.OrderRequestDto;
import com.example.confectionery.service.AsyncOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/async-orders")
@RequiredArgsConstructor
public class AsyncOrderController {

    private final AsyncOrderService asyncOrderService;

    @PostMapping
    public ResponseEntity<Map<String, UUID>> checkout(@RequestBody OrderRequestDto dto) {
        UUID taskId = UUID.randomUUID();
        asyncOrderService.createOrderAsync(taskId, dto);
        return ResponseEntity.accepted().body(Map.of("taskId", taskId));
    }

    @PostMapping("/test-real-race")
    public ResponseEntity<Map<String, Object>> testRealRace(@RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(asyncOrderService.realBusinessRaceTest(dto));
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<Object> getStatus(@PathVariable UUID taskId) {
        return ResponseEntity.ok(asyncOrderService.getStatus(taskId));
    }

    @GetMapping("/total-count")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(asyncOrderService.getTotalProcessedCount());
    }
}