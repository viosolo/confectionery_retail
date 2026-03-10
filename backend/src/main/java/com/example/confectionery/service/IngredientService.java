package com.example.confectionery.service;

import com.example.confectionery.dto.IngredientDto;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.IngredientDtoMapper;
import com.example.confectionery.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ProductService productService;
    private final IngredientDtoMapper ingredientMapper;

    private static final String INGREDIENT_NOT_FOUND_MSG = "Ингредиент с ID %d не найден";

    public List<IngredientDto> getAll() {

        log.info(">>> Fetching all ingredients list");
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientMapper)
                .toList();
    }

    public IngredientDto getById(Long id) {
        return ingredientRepository.findById(id)
                .map(ingredientMapper)
                .orElseThrow(() -> {
                    log.error(">>> Failed to find ingredient with ID: {}", id);
                    return new ResourceNotFoundException(String.format(INGREDIENT_NOT_FOUND_MSG, id));
                });
    }

    @Transactional
    public IngredientDto create(IngredientDto dto) {
        log.info(">>> Creating new ingredient: {}", dto.getName());
        if (ingredientRepository.existsByName(dto.getName())) {
            log.warn(">>> Ingredient creation failed: name '{}' already exists", dto.getName());
            throw new BadRequestException("Ингредиент с названием " + dto.getName() + " уже существует");
        }

        Ingredient entity = ingredientMapper.toEntity(dto);
        Ingredient saved = ingredientRepository.save(entity);
        log.info(">>> Ingredient '{}' successfully saved with ID: {}", saved.getName(), saved.getId());

        productService.invalidateCache();

        return ingredientMapper.apply(saved);
    }

    @Transactional
    public IngredientDto update(Long id, IngredientDto details) {
        log.info(">>> Updating ingredient ID: {}", id);

        Ingredient ingredient = findIngredientOrThrow(id, "Update");

        ingredient.setName(details.getName());
        ingredient.setDescription(details.getDescription());

        Ingredient updated = ingredientRepository.save(ingredient);
        productService.invalidateCache();
        log.info(">>> Ingredient ID {} updated successfully", id);
        return ingredientMapper.apply(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info(">>> Attempting to delete ingredient ID: {}", id);

        Ingredient ingredient = findIngredientOrThrow(id, "Deletion");

        int productCount = ingredient.getProducts().size();
        if (productCount > 0) {
            log.info(">>> Unlinking ingredient '{}' from {} products", ingredient.getName(), productCount);
        }

        for (Product product : ingredient.getProducts()) {
            product.getIngredients().remove(ingredient);
        }

        ingredientRepository.delete(ingredient);
        productService.invalidateCache();

        log.info(">>> Ingredient ID {} successfully deleted", id);
    }

    private Ingredient findIngredientOrThrow(Long id, String actionName) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(">>> {} failed: Ingredient ID {} not found", actionName, id);
                    return new ResourceNotFoundException(String.format(INGREDIENT_NOT_FOUND_MSG, id));
                });
    }
}