package com.example.confectionery.controller;

import com.example.confectionery.entity.Ingredient;
import com.example.confectionery.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAll() {
        return ingredientService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.getById(id));
    }

    @PostMapping
    public Ingredient create(@RequestBody Ingredient ingredient) {
        return ingredientService.create(ingredient);
    }

    @PutMapping("/{id}")
    public Ingredient update(@PathVariable Long id, @RequestBody Ingredient ingredient) {
        return ingredientService.update(id, ingredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
