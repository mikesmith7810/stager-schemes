package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Category;

public record CategoryResponse(Long id, String name) {

  public static CategoryResponse from(Category category) {
    return new CategoryResponse(category.getId(), category.getName());
  }
}
