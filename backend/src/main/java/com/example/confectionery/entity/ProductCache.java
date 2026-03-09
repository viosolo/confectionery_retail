package com.example.confectionery.entity;

import com.example.confectionery.dto.ProductResponse;
import com.example.confectionery.dto.ProductSearchKey;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductCache {


    private final Map<ProductSearchKey, Page<ProductResponse>> searchIndex = new ConcurrentHashMap<>();


    public Page<ProductResponse> get(ProductSearchKey key) {
        return searchIndex.get(key);
    }

    public boolean containsKey(ProductSearchKey key) {
        boolean exists = searchIndex.containsKey(key);
        if (exists) {
            log.info(">>> [CACHE HIT] Данные найдены в кэше для ключа: {}", key);
        }
        return exists;
    }

    public void put(ProductSearchKey key, Page<ProductResponse> value) {
        searchIndex.put(key, value);
    }

    public void clear() {
        searchIndex.clear();
    }
}
