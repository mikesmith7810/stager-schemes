package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.PackItemRequest;
import com.ms.stagerschemes.dto.PackRequest;
import com.ms.stagerschemes.dto.PackResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.PackItem;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackItemRepository;
import com.ms.stagerschemes.repository.PackRepository;
import com.ms.stagerschemes.repository.SchemeRoomPackRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PackService {

  private final PackRepository packRepository;
  private final PackItemRepository packItemRepository;
  private final ItemRepository itemRepository;
  private final SchemeRoomPackRepository schemeRoomPackRepository;

  public PackService(
      PackRepository packRepository,
      PackItemRepository packItemRepository,
      ItemRepository itemRepository,
      SchemeRoomPackRepository schemeRoomPackRepository) {
    this.packRepository = packRepository;
    this.packItemRepository = packItemRepository;
    this.itemRepository = itemRepository;
    this.schemeRoomPackRepository = schemeRoomPackRepository;
  }

  @Transactional(readOnly = true)
  public List<PackResponse> findAllPacks() {
    return packRepository.findAll().stream().map(PackResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public PackResponse findPackById(Long packId) {
    return packRepository
        .findById(packId)
        .map(PackResponse::from)
        .orElseThrow(() -> new NoSuchElementException("Pack not found: " + packId));
  }

  @Transactional
  public PackResponse createPack(PackRequest packRequest) {
    Pack savedPack = packRepository.save(new Pack(packRequest.name()));
    if (packRequest.items() != null) {
      packRequest.items().forEach(itemRequest -> addItemToPackInternal(savedPack, itemRequest));
    }
    return PackResponse.from(packRepository.findById(savedPack.getId()).orElseThrow());
  }

  @Transactional
  public PackResponse updatePack(Long packId, PackRequest packRequest) {
    Pack pack =
        packRepository
            .findById(packId)
            .orElseThrow(() -> new NoSuchElementException("Pack not found: " + packId));
    pack.setName(packRequest.name());
    return PackResponse.from(packRepository.save(pack));
  }

  @Transactional
  public void deletePack(Long packId) {
    if (!packRepository.existsById(packId)) {
      throw new NoSuchElementException("Pack not found: " + packId);
    }
    schemeRoomPackRepository.deleteAllByPackId(packId);
    packRepository.deleteById(packId);
  }

  @Transactional
  public PackResponse addItemToPack(Long packId, PackItemRequest packItemRequest) {
    Pack pack =
        packRepository
            .findById(packId)
            .orElseThrow(() -> new NoSuchElementException("Pack not found: " + packId));
    addItemToPackInternal(pack, packItemRequest);
    return PackResponse.from(packRepository.findById(packId).orElseThrow());
  }

  @Transactional
  public void removeItemFromPack(Long packId, Long itemId) {
    Pack pack =
        packRepository
            .findById(packId)
            .orElseThrow(() -> new NoSuchElementException("Pack not found: " + packId));
    PackItem packItem =
        pack.getPackItems().stream()
            .filter(pi -> pi.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Item not in pack: " + itemId));
    pack.getPackItems().remove(packItem);
  }

  private void addItemToPackInternal(Pack pack, PackItemRequest packItemRequest) {
    Item item =
        itemRepository
            .findById(packItemRequest.itemId())
            .orElseThrow(
                () -> new NoSuchElementException("Item not found: " + packItemRequest.itemId()));
    packItemRepository.save(new PackItem(pack, item, packItemRequest.quantity()));
  }
}
