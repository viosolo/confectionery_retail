package com.example.confectionery.service;

import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.entity.Product;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ингредиент с ID " + id + " не найден"));
    }

    @Transactional
    public Ingredient create(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public Ingredient update(Long id, Ingredient details) {
        Ingredient ingredient = getById(id);
        ingredient.setName(details.getName());
        ingredient.setDescription(details.getDescription());
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public void delete(Long id) {
        Ingredient ingredient = getById(id);

        // Разрываем связи с продуктами перед удалением (ManyToMany)
        for (Product product : ingredient.getProducts()) {
            product.getIngredients().remove(ingredient);
        }

        ingredientRepository.delete(ingredient);
    }
}