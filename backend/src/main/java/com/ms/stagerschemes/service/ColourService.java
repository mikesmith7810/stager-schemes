package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.ColourRequest;
import com.ms.stagerschemes.dto.ColourResponse;
import com.ms.stagerschemes.model.Colour;
import com.ms.stagerschemes.repository.ColourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColourService {

  private final ColourRepository colourRepository;

  public ColourService(ColourRepository colourRepository) {
    this.colourRepository = colourRepository;
  }

  @Transactional(readOnly = true)
  public List<ColourResponse> findAll() {
    return colourRepository.findAll().stream().map(ColourResponse::from).toList();
  }

  @Transactional
  public ColourResponse create(ColourRequest colourRequest) {
    if (colourRepository.existsByName(colourRequest.name())) {
      throw new IllegalArgumentException("Colour already exists: " + colourRequest.name());
    }
    return ColourResponse.from(colourRepository.save(new Colour(colourRequest.name())));
  }

  @Transactional
  public void delete(Long colourId) {
    if (!colourRepository.existsById(colourId)) {
      throw new NoSuchElementException("Colour not found: " + colourId);
    }
    colourRepository.deleteById(colourId);
  }
}
