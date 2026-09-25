package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.ItemRequest;
import com.ms.stagerschemes.dto.ItemResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/items")
public interface ItemApi {

  @GetMapping
  ResponseEntity<List<ItemResponse>> findAllItems();

  @GetMapping("/bin")
  ResponseEntity<List<ItemResponse>> findBinItems();

  @GetMapping("/{itemId}")
  ResponseEntity<ItemResponse> findItemById(@PathVariable Long itemId);

  @PostMapping
  ResponseEntity<ItemResponse> createItem(@RequestBody ItemRequest itemRequest);

  @PutMapping("/{itemId}")
  ResponseEntity<ItemResponse> updateItem(
      @PathVariable Long itemId, @RequestBody ItemRequest itemRequest);

  @DeleteMapping("/{itemId}")
  ResponseEntity<Void> deleteItem(@PathVariable Long itemId);

  @PutMapping("/{itemId}/restore")
  ResponseEntity<Void> restoreItem(@PathVariable Long itemId);

  @DeleteMapping("/bin")
  ResponseEntity<Void> emptyBin();

  @PostMapping(value = "/{itemId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<Void> uploadImage(
      @PathVariable Long itemId, @RequestParam("file") MultipartFile file) throws IOException;

  @GetMapping("/{itemId}/image")
  ResponseEntity<byte[]> getImage(@PathVariable Long itemId);

  @DeleteMapping("/{itemId}/image")
  ResponseEntity<Void> deleteImage(@PathVariable Long itemId);
}
