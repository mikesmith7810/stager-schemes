package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.Colour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColourRepository extends JpaRepository<Colour, Long> {

  boolean existsByName(String name);
}
