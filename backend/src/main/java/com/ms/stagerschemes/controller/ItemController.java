package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.ItemApi;
import com.ms.stagerschemes.dto.ItemImageData;
import com.ms.stagerschemes.dto.ItemRequest;
import com.ms.stagerschemes.dto.ItemResponse;
import com.ms.stagerschemes.service.ItemService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ItemController implements ItemApi {

  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @Override
  public ResponseEntity<List<ItemResponse>> findAllItems() {
    return ResponseEntity.ok(itemService.findAllItems());
  }

  @Override
  public ResponseEntity<List<ItemResponse>> findBinItems() {
    return ResponseEntity.ok(itemService.findBinItems());
  }

  @Override
  public ResponseEntity<ItemResponse> findItemById(Long itemId) {
    return ResponseEntity.ok(itemService.findItemById(itemId));
  }

  @Override
  public ResponseEntity<ItemResponse> createItem(ItemRequest itemRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemRequest));
  }

  @Override
  public ResponseEntity<ItemResponse> updateItem(Long itemId, ItemRequest itemRequest) {
    return ResponseEntity.ok(itemService.updateItem(itemId, itemRequest));
  }

  @Override
  public ResponseEntity<Void> deleteItem(Long itemId) {
    itemService.deleteItem(itemId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> restoreItem(Long itemId) {
    itemService.restoreItem(itemId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> emptyBin() {
    itemService.emptyBin();
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> uploadImage(Long itemId, MultipartFile file) throws IOException {
    itemService.uploadImage(itemId, file);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<byte[]> getImage(Long itemId) {
    ItemImageData image = itemService.getImage(itemId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(image.mimeType()))
        .body(image.data());
  }

  @Override
  public ResponseEntity<Void> deleteImage(Long itemId) {
    itemService.deleteImage(itemId);
    return ResponseEntity.noContent().build();
  }
}
