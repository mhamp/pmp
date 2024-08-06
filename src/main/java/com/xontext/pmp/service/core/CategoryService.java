package com.xontext.pmp.service.core;

import com.xontext.pmp.model.Category;
import com.xontext.pmp.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    public final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public boolean categoryExists(Category category) {
        return categoryRepository.existsByName(category.getName());
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public Category findById(Long categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Cateogry not found"));
    }
    public Category findByCategoryName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
//    public List<Category> findByProfileId(Long profileId){
//        return categoryRepository.findByProfileId(profileId);
//    }
    public Category updateCategory(Long categoryId, Category category) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Attribute not found"));
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setMandatory(category.isMandatory());
        return categoryRepository.save(existingCategory);
    }
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }


}
