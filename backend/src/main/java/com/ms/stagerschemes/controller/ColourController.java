package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.ColourApi;
import com.ms.stagerschemes.dto.ColourRequest;
import com.ms.stagerschemes.dto.ColourResponse;
import com.ms.stagerschemes.service.ColourService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ColourController implements ColourApi {

  private final ColourService colourService;

  public ColourController(ColourService colourService) {
    this.colourService = colourService;
  }

  @Override
  public ResponseEntity<List<ColourResponse>> findAllColours() {
    return ResponseEntity.ok(colourService.findAll());
  }

  @Override
  public ResponseEntity<ColourResponse> createColour(ColourRequest colourRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(colourService.create(colourRequest));
  }

  @Override
  public ResponseEntity<Void> deleteColour(Long colourId) {
    colourService.delete(colourId);
    return ResponseEntity.noContent().build();
  }
}
