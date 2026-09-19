package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.PackApi;
import com.ms.stagerschemes.dto.PackItemRequest;
import com.ms.stagerschemes.dto.PackRequest;
import com.ms.stagerschemes.dto.PackResponse;
import com.ms.stagerschemes.service.PackService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PackController implements PackApi {

  private final PackService packService;

  public PackController(PackService packService) {
    this.packService = packService;
  }

  @Override
  public ResponseEntity<List<PackResponse>> findAllPacks() {
    return ResponseEntity.ok(packService.findAllPacks());
  }

  @Override
  public ResponseEntity<PackResponse> findPackById(Long packId) {
    return ResponseEntity.ok(packService.findPackById(packId));
  }

  @Override
  public ResponseEntity<PackResponse> createPack(PackRequest packRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(packService.createPack(packRequest));
  }

  @Override
  public ResponseEntity<PackResponse> updatePack(Long packId, PackRequest packRequest) {
    return ResponseEntity.ok(packService.updatePack(packId, packRequest));
  }

  @Override
  public ResponseEntity<Void> deletePack(Long packId) {
    packService.deletePack(packId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PackResponse> addItemToPack(Long packId, PackItemRequest packItemRequest) {
    return ResponseEntity.ok(packService.addItemToPack(packId, packItemRequest));
  }

  @Override
  public ResponseEntity<Void> removeItemFromPack(Long packId, Long itemId) {
    packService.removeItemFromPack(packId, itemId);
    return ResponseEntity.noContent().build();
  }
}
