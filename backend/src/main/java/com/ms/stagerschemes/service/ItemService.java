package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.ItemImageData;
import com.ms.stagerschemes.dto.ItemRequest;
import com.ms.stagerschemes.dto.ItemResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.ItemImage;
import com.ms.stagerschemes.repository.ItemImageRepository;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackItemRepository;
import com.ms.stagerschemes.repository.RoomItemRepository;
import com.ms.stagerschemes.repository.SchemeRoomItemRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ItemService {

  private final ItemRepository itemRepository;
  private final ItemImageRepository itemImageRepository;
  private final RoomItemRepository roomItemRepository;
  private final PackItemRepository packItemRepository;
  private final SchemeRoomItemRepository schemeRoomItemRepository;

  public ItemService(
      ItemRepository itemRepository,
      ItemImageRepository itemImageRepository,
      RoomItemRepository roomItemRepository,
      PackItemRepository packItemRepository,
      SchemeRoomItemRepository schemeRoomItemRepository) {
    this.itemRepository = itemRepository;
    this.itemImageRepository = itemImageRepository;
    this.roomItemRepository = roomItemRepository;
    this.packItemRepository = packItemRepository;
    this.schemeRoomItemRepository = schemeRoomItemRepository;
  }

  @Transactional(readOnly = true)
  public List<ItemResponse> findAllItems() {
    List<Item> items = itemRepository.findAll();
    List<Long> ids = items.stream().map(Item::getId).toList();
    Set<Long> idsWithImages = ids.isEmpty() ? Set.of() : itemImageRepository.findExistingIds(ids);
    return items.stream()
        .map(item -> ItemResponse.from(item, idsWithImages.contains(item.getId())))
        .toList();
  }

  @Transactional(readOnly = true)
  public ItemResponse findItemById(Long itemId) {
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
    return ItemResponse.from(item, itemImageRepository.existsById(itemId));
  }

  @Transactional
  public ItemResponse createItem(ItemRequest itemRequest) {
    Item savedItem =
        itemRepository.save(
            new Item(itemRequest.name(), itemRequest.price(), itemRequest.webLink(), itemRequest.category()));
    return ItemResponse.from(savedItem, false);
  }

  @Transactional
  public ItemResponse updateItem(Long itemId, ItemRequest itemRequest) {
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
    item.setName(itemRequest.name());
    item.setPrice(itemRequest.price());
    item.setWebLink(itemRequest.webLink());
    item.setCategory(itemRequest.category());
    return ItemResponse.from(itemRepository.save(item), itemImageRepository.existsById(itemId));
  }

  @Transactional
  public void deleteItem(Long itemId) {
    if (!itemRepository.existsById(itemId)) {
      throw new NoSuchElementException("Item not found: " + itemId);
    }
    List<String> roomNames =
        roomItemRepository.findByItemId(itemId).stream()
            .map(roomItem -> roomItem.getRoom().getName())
            .distinct()
            .sorted()
            .toList();
    List<String> packNames =
        packItemRepository.findByItemId(itemId).stream()
            .map(packItem -> packItem.getPack().getName())
            .distinct()
            .sorted()
            .toList();
    List<String> schemeNames =
        schemeRoomItemRepository.findByItemId(itemId).stream()
            .map(sri -> sri.getSchemeRoom().getScheme().getName())
            .distinct()
            .sorted()
            .toList();
    if (!roomNames.isEmpty() || !packNames.isEmpty() || !schemeNames.isEmpty()) {
      List<String> parts = new ArrayList<>();
      if (!roomNames.isEmpty()) {
        parts.add("room template(s): " + String.join(", ", roomNames));
      }
      if (!packNames.isEmpty()) {
        parts.add("pack(s): " + String.join(", ", packNames));
      }
      if (!schemeNames.isEmpty()) {
        parts.add("scheme(s): " + String.join(", ", schemeNames));
      }
      throw new IllegalArgumentException(
          "Cannot delete: this item is used in " + String.join("; ", parts) + ".");
    }
    itemImageRepository.findById(itemId).ifPresent(itemImageRepository::delete);
    itemRepository.deleteById(itemId);
  }

  @Transactional
  public void uploadImage(Long itemId, MultipartFile file) throws IOException {
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
    byte[] bytes = file.getBytes();
    String mimeType = file.getContentType();
    ItemImage image =
        itemImageRepository.findById(itemId).orElse(new ItemImage(item, bytes, mimeType));
    image.setData(bytes);
    image.setMimeType(mimeType);
    itemImageRepository.save(image);
  }

  @Transactional(readOnly = true)
  public ItemImageData getImage(Long itemId) {
    ItemImage image =
        itemImageRepository
            .findById(itemId)
            .orElseThrow(() -> new NoSuchElementException("No image for item: " + itemId));
    return new ItemImageData(image.getData(), image.getMimeType());
  }

  @Transactional
  public void deleteImage(Long itemId) {
    if (!itemRepository.existsById(itemId)) {
      throw new NoSuchElementException("Item not found: " + itemId);
    }
    itemImageRepository.findById(itemId).ifPresent(itemImageRepository::delete);
  }
}
