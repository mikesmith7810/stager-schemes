package com.ms.stagerschemes.config;

import com.ms.stagerschemes.model.Category;
import com.ms.stagerschemes.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDataSeeder implements ApplicationRunner {

  private static final List<String> DEFAULT_CATEGORIES =
      List.of(
          "Beds",
          "Wardrobes",
          "Appliances",
          "Arm Chairs",
          "Dining Chairs",
          "Tables",
          "Lamps",
          "Soft Furnishings",
          "Kitchen",
          "Plants",
          "Artwork",
          "Mirrors");

  private final CategoryRepository categoryRepository;

  public ReferenceDataSeeder(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (categoryRepository.count() == 0) {
      DEFAULT_CATEGORIES.stream().map(Category::new).forEach(categoryRepository::save);
    }
  }
}
