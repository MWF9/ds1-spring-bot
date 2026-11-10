package com.mwf9.projet.service;

import com.mwf9.projet.exception.ResourceNotFoundException;
import com.mwf9.projet.model.Category;
import com.mwf9.projet.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public Category create(Category category) {
        category.setId(null);
        return categoryRepository.saveAndFlush(category);
    }

    public Category update(Long id, Category data) {
        Category category = findById(id);
        category.setName(data.getName());
        category.setDescription(data.getDescription());
        return categoryRepository.saveAndFlush(category);
    }

    public void delete(Long id) {
        categoryRepository.delete(findById(id));
        categoryRepository.flush(); // fails with a 409 if products still use this category
    }
}
