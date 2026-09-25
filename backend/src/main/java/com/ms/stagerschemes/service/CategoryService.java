package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.CategoryRequest;
import com.ms.stagerschemes.dto.CategoryResponse;
import com.ms.stagerschemes.model.Category;
import com.ms.stagerschemes.repository.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> findAll() {
    return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
  }

  @Transactional
  public CategoryResponse create(CategoryRequest categoryRequest) {
    if (categoryRepository.existsByName(categoryRequest.name())) {
      throw new IllegalArgumentException("Category already exists: " + categoryRequest.name());
    }
    return CategoryResponse.from(categoryRepository.save(new Category(categoryRequest.name())));
  }

  @Transactional
  public void delete(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new NoSuchElementException("Category not found: " + categoryId);
    }
    categoryRepository.deleteById(categoryId);
  }
}
