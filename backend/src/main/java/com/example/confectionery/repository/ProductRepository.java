package com.example.confectionery.repository;

import com.example.confectionery.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @NonNull
    List<Product> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category", "ingredients"})
    Optional<Product> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"category", "ingredients"})
    Optional<Product> findByName(String name);

    @EntityGraph(attributePaths = {"category", "ingredients"})
    List<Product> findAllByActiveTrue();

    @EntityGraph(attributePaths = {"category", "ingredients"})
    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE " +
            "(:slug IS NULL OR p.category.slug = :slug) AND " +
            "(:flavors IS NULL OR p.flavor IN :flavors) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "p.active = true")
    Page<Product> findByComplexFilters(
            @Param("slug") String slug,
            @Param("flavors") List<String> flavors,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );


    @Query(value = "SELECT p.* FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "WHERE c.slug = :slug " +
            "AND (:flavors IS NULL OR p.flavor IN :flavors) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)",
            nativeQuery = true)
    Page<Product> findByComplexFiltersNative(@Param("slug") String slug,
                                             @Param("flavors") List<String> flavors,
                                             @Param("maxPrice") Double maxPrice,
                                             Pageable pageable);
}