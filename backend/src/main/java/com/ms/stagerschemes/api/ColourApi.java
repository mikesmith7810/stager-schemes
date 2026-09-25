package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.ColourRequest;
import com.ms.stagerschemes.dto.ColourResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/colours")
public interface ColourApi {

  @GetMapping
  ResponseEntity<List<ColourResponse>> findAllColours();

  @PostMapping
  ResponseEntity<ColourResponse> createColour(@RequestBody ColourRequest colourRequest);

  @DeleteMapping("/{colourId}")
  ResponseEntity<Void> deleteColour(@PathVariable Long colourId);
}
