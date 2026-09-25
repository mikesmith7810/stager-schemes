package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.CategoryRequest;
import com.ms.stagerschemes.dto.CategoryResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/categories")
public interface CategoryApi {

  @GetMapping
  ResponseEntity<List<CategoryResponse>> findAllCategories();

  @PostMapping
  ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest);

  @DeleteMapping("/{categoryId}")
  ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId);
}
