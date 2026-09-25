package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.CategoryApi;
import com.ms.stagerschemes.dto.CategoryRequest;
import com.ms.stagerschemes.dto.CategoryResponse;
import com.ms.stagerschemes.service.CategoryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryApi {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  public ResponseEntity<List<CategoryResponse>> findAllCategories() {
    return ResponseEntity.ok(categoryService.findAll());
  }

  @Override
  public ResponseEntity<CategoryResponse> createCategory(CategoryRequest categoryRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(categoryRequest));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(Long categoryId) {
    categoryService.delete(categoryId);
    return ResponseEntity.noContent().build();
  }
}
