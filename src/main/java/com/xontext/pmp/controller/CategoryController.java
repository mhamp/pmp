package com.xontext.pmp.controller;

import com.xontext.pmp.model.Category;
import com.xontext.pmp.service.core.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * These are only general categories preset for every user and profile the same way.
 */
@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {

    public final CategoryService categoryService;
    @PostMapping
    public ResponseEntity createCategory(@RequestBody Category category){
        try {
            if (categoryService.categoryExists(category)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Category with this name already exists.");
            } else {
                Category savedCategory = categoryService.createCategory(category);
                return ResponseEntity.ok(savedCategory);
            }
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity getCategoryById(@PathVariable Long id){
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping
    public ResponseEntity getCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateCategory(@PathVariable Long id, @RequestBody Category category){
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
