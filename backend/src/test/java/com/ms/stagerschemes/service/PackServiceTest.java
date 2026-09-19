package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.PackItemRequest;
import com.ms.stagerschemes.dto.PackRequest;
import com.ms.stagerschemes.dto.PackResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.PackItem;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackItemRepository;
import com.ms.stagerschemes.repository.PackRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PackServiceTest {

  @Mock private PackRepository packRepository;
  @Mock private PackItemRepository packItemRepository;
  @Mock private ItemRepository itemRepository;

  private PackService packService;

  @BeforeEach
  void setUp() {
    packService = new PackService(packRepository, packItemRepository, itemRepository);
  }

  @Test
  void findAllPacks_returnsAllPacksAsDtos() {
    when(packRepository.findAll()).thenReturn(List.of(new Pack("Bedroom Pack")));

    List<PackResponse> result = packService.findAllPacks();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Bedroom Pack");
  }

  @Test
  void findPackById_existingPack_returnsPackResponse() {
    when(packRepository.findById(1L)).thenReturn(Optional.of(new Pack("Living Room Pack")));

    PackResponse result = packService.findPackById(1L);

    assertThat(result.name()).isEqualTo("Living Room Pack");
  }

  @Test
  void findPackById_nonExistingPack_throwsNoSuchElementException() {
    when(packRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> packService.findPackById(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }

  @Test
  void createPack_withNoItems_savesPackSuccessfully() {
    PackRequest packRequest = new PackRequest("New Pack", null);
    Pack savedPack = new Pack("New Pack");
    when(packRepository.save(any(Pack.class))).thenReturn(savedPack);
    when(packRepository.findById(any())).thenReturn(Optional.of(savedPack));

    PackResponse result = packService.createPack(packRequest);

    assertThat(result.name()).isEqualTo("New Pack");
    verify(packRepository).save(any(Pack.class));
  }

  @Test
  void updatePack_existingPack_updatesName() {
    Pack existingPack = new Pack("Old Name");
    PackRequest updateRequest = new PackRequest("New Name", null);
    when(packRepository.findById(1L)).thenReturn(Optional.of(existingPack));
    when(packRepository.save(any(Pack.class))).thenReturn(existingPack);

    PackResponse result = packService.updatePack(1L, updateRequest);

    assertThat(result.name()).isEqualTo("New Name");
  }

  @Test
  void deletePack_existingPack_deletesSuccessfully() {
    when(packRepository.existsById(1L)).thenReturn(true);

    packService.deletePack(1L);

    verify(packRepository).deleteById(1L);
  }

  @Test
  void deletePack_nonExistingPack_throwsNoSuchElementException() {
    when(packRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> packService.deletePack(99L))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void addItemToPack_existingPackAndItem_savesPackItemAndReturnsUpdatedPack() {
    Pack pack = new Pack("Pack");
    Item chair = new Item("Chair", new BigDecimal("100.00"), null);
    PackItemRequest packItemRequest = new PackItemRequest(1L, 2);
    when(packRepository.findById(1L)).thenReturn(Optional.of(pack));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(chair));
    when(packItemRepository.save(any(PackItem.class))).thenReturn(new PackItem(pack, chair, 2));
    when(packRepository.findById(1L)).thenReturn(Optional.of(pack));

    packService.addItemToPack(1L, packItemRequest);

    verify(packItemRepository).save(any(PackItem.class));
  }

  @Test
  void removeItemFromPack_itemInPack_deletesPackItem() {
    Pack pack = new Pack("Pack");
    Item chair = new Item("Chair", new BigDecimal("100.00"), null);
    PackItem packItem = new PackItem(pack, chair, 1);
    pack.getPackItems().add(packItem);

    when(packRepository.findById(1L)).thenReturn(Optional.of(pack));
    when(itemRepository.findById(any())).thenReturn(Optional.of(chair));

    packService.removeItemFromPack(1L, chair.getId());

    verify(packItemRepository).delete(packItem);
  }
}
