package com.example.confectionery;

import com.example.confectionery.controller.CategoryController;
import com.example.confectionery.entity.Category;
import com.example.confectionery.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void createCategory_ReturnsStatusCreated() throws Exception {
        Category category = Category.builder().id(1L).name("Торты").slug("torty").build();
        when(categoryService.saveCategory(any())).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Торты\", \"slug\": \"torty\"}"))
                .andExpect(status().isOk()) // или isCreated(), зависит от твоего контроллера
                .andExpect(jsonPath("$.name").value("Торты"));
    }
}
