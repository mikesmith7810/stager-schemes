package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.CategoryRequest;
import com.ms.stagerschemes.dto.CategoryResponse;
import com.ms.stagerschemes.model.Category;
import com.ms.stagerschemes.repository.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;

  private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    categoryService = new CategoryService(categoryRepository);
  }

  @Test
  void findAll_returnsAllCategoriesAsDtos() {
    when(categoryRepository.findAll()).thenReturn(List.of(new Category("Beds")));

    List<CategoryResponse> result = categoryService.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Beds");
  }

  @Test
  void create_newCategory_savesAndReturnsResponse() {
    when(categoryRepository.existsByName("Tables")).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenReturn(new Category("Tables"));

    CategoryResponse result = categoryService.create(new CategoryRequest("Tables"));

    assertThat(result.name()).isEqualTo("Tables");
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  void create_duplicateName_throwsIllegalArgumentException() {
    when(categoryRepository.existsByName("Beds")).thenReturn(true);

    assertThatThrownBy(() -> categoryService.create(new CategoryRequest("Beds")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Beds");
  }

  @Test
  void delete_existingCategory_deletesSuccessfully() {
    when(categoryRepository.existsById(1L)).thenReturn(true);

    categoryService.delete(1L);

    verify(categoryRepository).deleteById(1L);
  }

  @Test
  void delete_nonExistingCategory_throwsNoSuchElementException() {
    when(categoryRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> categoryService.delete(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }
}
