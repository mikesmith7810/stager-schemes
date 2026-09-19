package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.ItemRequest;
import com.ms.stagerschemes.dto.ItemResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.repository.ItemImageRepository;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackItemRepository;
import com.ms.stagerschemes.repository.RoomItemRepository;
import com.ms.stagerschemes.repository.SchemeRoomItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  @Mock private ItemRepository itemRepository;
  @Mock private ItemImageRepository itemImageRepository;
  @Mock private RoomItemRepository roomItemRepository;
  @Mock private PackItemRepository packItemRepository;
  @Mock private SchemeRoomItemRepository schemeRoomItemRepository;

  private ItemService itemService;

  @BeforeEach
  void setUp() {
    itemService =
        new ItemService(
            itemRepository,
            itemImageRepository,
            roomItemRepository,
            packItemRepository,
            schemeRoomItemRepository);
  }

  @Test
  void findAllItems_returnsAllItemsAsDtos() {
    Item chair = new Item("Chair", new BigDecimal("150.00"), "https://example.com/chair", null);
    when(itemRepository.findAll()).thenReturn(List.of(chair));
    when(itemImageRepository.findExistingIds(any())).thenReturn(Set.of());

    List<ItemResponse> result = itemService.findAllItems();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Chair");
    assertThat(result.get(0).price()).isEqualByComparingTo("150.00");
  }

  @Test
  void findItemById_existingItem_returnsItemResponse() {
    Item lamp = new Item("Lamp", new BigDecimal("75.00"), null, null);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(lamp));

    ItemResponse result = itemService.findItemById(1L);

    assertThat(result.name()).isEqualTo("Lamp");
  }

  @Test
  void findItemById_nonExistingItem_throwsNoSuchElementException() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> itemService.findItemById(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }

  @Test
  void createItem_savesAndReturnsNewItem() {
    ItemRequest itemRequest = new ItemRequest("Sofa", new BigDecimal("500.00"), null, null);
    Item savedItem = new Item("Sofa", new BigDecimal("500.00"), null, null);
    when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

    ItemResponse result = itemService.createItem(itemRequest);

    assertThat(result.name()).isEqualTo("Sofa");
    verify(itemRepository).save(any(Item.class));
  }

  @Test
  void updateItem_existingItem_updatesAndReturnsItem() {
    Item existingItem = new Item("Old Name", new BigDecimal("100.00"), null, null);
    ItemRequest updateRequest =
        new ItemRequest("New Name", new BigDecimal("120.00"), "https://example.com", null);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
    when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

    ItemResponse result = itemService.updateItem(1L, updateRequest);

    assertThat(result.name()).isEqualTo("New Name");
    assertThat(result.price()).isEqualByComparingTo("120.00");
  }

  @Test
  void updateItem_nonExistingItem_throwsNoSuchElementException() {
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> itemService.updateItem(99L, new ItemRequest("Name", BigDecimal.ONE, null, null)))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void deleteItem_existingItem_deletesSuccessfully() {
    when(itemRepository.existsById(1L)).thenReturn(true);
    when(roomItemRepository.findByItemId(1L)).thenReturn(List.of());
    when(packItemRepository.findByItemId(1L)).thenReturn(List.of());
    when(schemeRoomItemRepository.findByItemId(1L)).thenReturn(List.of());
    when(itemImageRepository.findById(1L)).thenReturn(Optional.empty());

    itemService.deleteItem(1L);

    verify(itemRepository).deleteById(1L);
  }

  @Test
  void deleteItem_nonExistingItem_throwsNoSuchElementException() {
    when(itemRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> itemService.deleteItem(99L))
        .isInstanceOf(NoSuchElementException.class);
  }
}
