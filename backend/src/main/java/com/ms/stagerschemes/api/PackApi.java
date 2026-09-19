package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.PackItemRequest;
import com.ms.stagerschemes.dto.PackRequest;
import com.ms.stagerschemes.dto.PackResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/packs")
public interface PackApi {

  @GetMapping
  ResponseEntity<List<PackResponse>> findAllPacks();

  @GetMapping("/{packId}")
  ResponseEntity<PackResponse> findPackById(@PathVariable Long packId);

  @PostMapping
  ResponseEntity<PackResponse> createPack(@RequestBody PackRequest packRequest);

  @PutMapping("/{packId}")
  ResponseEntity<PackResponse> updatePack(
      @PathVariable Long packId, @RequestBody PackRequest packRequest);

  @DeleteMapping("/{packId}")
  ResponseEntity<Void> deletePack(@PathVariable Long packId);

  @PostMapping("/{packId}/items")
  ResponseEntity<PackResponse> addItemToPack(
      @PathVariable Long packId, @RequestBody PackItemRequest packItemRequest);

  @DeleteMapping("/{packId}/items/{itemId}")
  ResponseEntity<Void> removeItemFromPack(@PathVariable Long packId, @PathVariable Long itemId);
}
