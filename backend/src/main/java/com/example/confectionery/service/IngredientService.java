package com.example.confectionery.service;

import com.example.confectionery.dto.IngredientDto;
import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.mapper.IngredientDtoMapper;
import com.example.confectionery.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ProductService productService;
    private final IngredientDtoMapper ingredientMapper;
    private final IngredientDtoMapper ingredientDtoMapper;
    private static final String INGREDIENT_NOT_FOUND_MSG = "Ингредиент с ID не найден";

    public List<IngredientDto> getAll() {
        return ingredientRepository.findAll()
                .stream()
                .map(ingredientMapper)
                .toList();
    }

    public IngredientDto getById(Long id) {
        return ingredientRepository.findById(id)
                .map(ingredientMapper)
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MSG));
    }

    @Transactional
    public IngredientDto create(IngredientDto dto) {
        Ingredient entity = ingredientMapper.toEntity(dto);
        Ingredient saved = ingredientRepository.save(entity);
        productService.invalidateCache();
        return ingredientDtoMapper.apply(saved);
    }

    @Transactional
    public IngredientDto update(Long id, IngredientDto details) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MSG));

        ingredient.setName(details.getName());
        ingredient.setDescription(details.getDescription());

        Ingredient updated = ingredientRepository.save(ingredient);
        productService.invalidateCache();
        return ingredientDtoMapper.apply(updated);
    }

    @Transactional
    public void delete(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MSG));

        for (Product product : ingredient.getProducts()) {
            product.getIngredients().remove(ingredient);
        }
        ingredientRepository.delete(ingredient);
        productService.invalidateCache();
    }
}