package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.SchemeApi;
import com.ms.stagerschemes.dto.AddItemToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddPackToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddRoomToSchemeRequest;
import com.ms.stagerschemes.dto.SchemeRequest;
import com.ms.stagerschemes.dto.SchemeResponse;
import com.ms.stagerschemes.dto.SchemeRoomSummary;
import com.ms.stagerschemes.dto.SchemeSummaryResponse;
import com.ms.stagerschemes.service.SchemeService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchemeController implements SchemeApi {

  private final SchemeService schemeService;

  public SchemeController(SchemeService schemeService) {
    this.schemeService = schemeService;
  }

  @Override
  public ResponseEntity<List<SchemeResponse>> findAllSchemes() {
    return ResponseEntity.ok(schemeService.findAllSchemes());
  }

  @Override
  public ResponseEntity<SchemeResponse> findSchemeById(Long schemeId) {
    return ResponseEntity.ok(schemeService.findSchemeById(schemeId));
  }

  @Override
  public ResponseEntity<SchemeResponse> createScheme(SchemeRequest schemeRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(schemeService.createScheme(schemeRequest));
  }

  @Override
  public ResponseEntity<SchemeResponse> updateScheme(Long schemeId, SchemeRequest schemeRequest) {
    return ResponseEntity.ok(schemeService.updateScheme(schemeId, schemeRequest));
  }

  @Override
  public ResponseEntity<Void> deleteScheme(Long schemeId) {
    schemeService.deleteScheme(schemeId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<SchemeSummaryResponse> getSchemeSummary(Long schemeId) {
    return ResponseEntity.ok(schemeService.getSchemeSummary(schemeId));
  }

  @Override
  public ResponseEntity<SchemeRoomSummary> addRoomToScheme(
      Long schemeId, AddRoomToSchemeRequest addRoomToSchemeRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(schemeService.addRoomToScheme(schemeId, addRoomToSchemeRequest));
  }

  @Override
  public ResponseEntity<Void> removeRoomFromScheme(Long schemeId, Long schemeRoomId) {
    schemeService.removeRoomFromScheme(schemeId, schemeRoomId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<SchemeRoomSummary> addItemToSchemeRoom(
      Long schemeId, Long schemeRoomId, AddItemToSchemeRoomRequest addItemToSchemeRoomRequest) {
    return ResponseEntity.ok(
        schemeService.addItemToSchemeRoom(schemeRoomId, addItemToSchemeRoomRequest));
  }

  @Override
  public ResponseEntity<Void> removeItemFromSchemeRoom(
      Long schemeId, Long schemeRoomId, Long itemId) {
    schemeService.removeItemFromSchemeRoom(schemeRoomId, itemId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<SchemeRoomSummary> addPackToSchemeRoom(
      Long schemeId, Long schemeRoomId, AddPackToSchemeRoomRequest addPackToSchemeRoomRequest) {
    return ResponseEntity.ok(
        schemeService.addPackToSchemeRoom(schemeRoomId, addPackToSchemeRoomRequest));
  }

  @Override
  public ResponseEntity<Void> removePackFromSchemeRoom(
      Long schemeId, Long schemeRoomId, Long packId) {
    schemeService.removePackFromSchemeRoom(schemeRoomId, packId);
    return ResponseEntity.noContent().build();
  }
}
