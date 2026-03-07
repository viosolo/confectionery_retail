package com.example.confectionery.dto;

import java.util.List;
import java.util.Objects;

public class ProductSearchKey {
    private final String slug;
    private final List<String> flavors;
    private final Double maxPrice;
    private final int page;
    private final int size;
    private final String sort;

    public ProductSearchKey(String slug, List<String> flavors, Double maxPrice, int page, int size, String sort) {
        this.slug = slug;
        this.flavors = flavors;
        this.maxPrice = maxPrice;
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProductSearchKey that = (ProductSearchKey) o;
        return page == that.page &&
                size == that.size &&
                Objects.equals(slug, that.slug) &&
                Objects.equals(flavors, that.flavors) &&
                Objects.equals(maxPrice, that.maxPrice) &&
                Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, flavors, maxPrice, page, size, sort);
    }
}
