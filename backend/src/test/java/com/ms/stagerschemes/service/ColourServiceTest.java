package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.ColourRequest;
import com.ms.stagerschemes.dto.ColourResponse;
import com.ms.stagerschemes.model.Colour;
import com.ms.stagerschemes.repository.ColourRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ColourServiceTest {

  @Mock private ColourRepository colourRepository;

  private ColourService colourService;

  @BeforeEach
  void setUp() {
    colourService = new ColourService(colourRepository);
  }

  @Test
  void findAll_returnsAllColoursAsDtos() {
    when(colourRepository.findAll()).thenReturn(List.of(new Colour("White")));

    List<ColourResponse> result = colourService.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("White");
  }

  @Test
  void create_newColour_savesAndReturnsResponse() {
    when(colourRepository.existsByName("Navy")).thenReturn(false);
    when(colourRepository.save(any(Colour.class))).thenReturn(new Colour("Navy"));

    ColourResponse result = colourService.create(new ColourRequest("Navy"));

    assertThat(result.name()).isEqualTo("Navy");
    verify(colourRepository).save(any(Colour.class));
  }

  @Test
  void create_duplicateName_throwsIllegalArgumentException() {
    when(colourRepository.existsByName("White")).thenReturn(true);

    assertThatThrownBy(() -> colourService.create(new ColourRequest("White")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("White");
  }

  @Test
  void delete_existingColour_deletesSuccessfully() {
    when(colourRepository.existsById(1L)).thenReturn(true);

    colourService.delete(1L);

    verify(colourRepository).deleteById(1L);
  }

  @Test
  void delete_nonExistingColour_throwsNoSuchElementException() {
    when(colourRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> colourService.delete(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }
}
