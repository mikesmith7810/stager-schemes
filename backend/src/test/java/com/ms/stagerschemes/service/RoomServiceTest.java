package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.RoomRequest;
import com.ms.stagerschemes.dto.RoomResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.Room;
import com.ms.stagerschemes.model.RoomItem;
import com.ms.stagerschemes.model.RoomPack;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackRepository;
import com.ms.stagerschemes.repository.RoomItemRepository;
import com.ms.stagerschemes.repository.RoomPackRepository;
import com.ms.stagerschemes.repository.RoomRepository;
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
class RoomServiceTest {

  @Mock private RoomRepository roomRepository;
  @Mock private RoomItemRepository roomItemRepository;
  @Mock private RoomPackRepository roomPackRepository;
  @Mock private ItemRepository itemRepository;
  @Mock private PackRepository packRepository;

  private RoomService roomService;

  @BeforeEach
  void setUp() {
    roomService =
        new RoomService(
            roomRepository, roomItemRepository, roomPackRepository, itemRepository, packRepository);
  }

  @Test
  void findAllRooms_returnsAllRoomsAsDtos() {
    when(roomRepository.findAll()).thenReturn(List.of(new Room("Master Bedroom")));

    List<RoomResponse> result = roomService.findAllRooms();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Master Bedroom");
  }

  @Test
  void findRoomById_existingRoom_returnsRoomResponse() {
    when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room("Dining Room")));

    RoomResponse result = roomService.findRoomById(1L);

    assertThat(result.name()).isEqualTo("Dining Room");
  }

  @Test
  void findRoomById_nonExistingRoom_throwsNoSuchElementException() {
    when(roomRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> roomService.findRoomById(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }

  @Test
  void createRoom_withNoItemsOrPacks_savesAndReturnsRoom() {
    RoomRequest roomRequest = new RoomRequest("Kitchen", null, null);
    Room savedRoom = new Room("Kitchen");
    when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);
    when(roomRepository.findById(any())).thenReturn(Optional.of(savedRoom));

    RoomResponse result = roomService.createRoom(roomRequest);

    assertThat(result.name()).isEqualTo("Kitchen");
  }

  @Test
  void updateRoom_existingRoom_updatesName() {
    Room existingRoom = new Room("Old Name");
    when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
    when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);

    roomService.updateRoom(1L, new RoomRequest("New Name", null, null));

    assertThat(existingRoom.getName()).isEqualTo("New Name");
  }

  @Test
  void deleteRoom_existingRoom_deletesSuccessfully() {
    when(roomRepository.existsById(1L)).thenReturn(true);

    roomService.deleteRoom(1L);

    verify(roomRepository).deleteById(1L);
  }

  @Test
  void deleteRoom_nonExistingRoom_throwsNoSuchElementException() {
    when(roomRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> roomService.deleteRoom(99L))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void addItemToRoom_existingRoomAndItem_savesRoomItemAndReturnsRoom() {
    Room room = new Room("Living Room");
    Item sofa = new Item("Sofa", new BigDecimal("400.00"), null, null);
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(sofa));
    when(roomItemRepository.save(any(RoomItem.class))).thenReturn(new RoomItem(room, sofa, 1));
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

    roomService.addItemToRoom(1L, new com.ms.stagerschemes.dto.RoomItemRequest(1L, 1));

    verify(roomItemRepository).save(any(RoomItem.class));
  }

  @Test
  void removeItemFromRoom_itemInRoom_removesRoomItemFromCollection() {
    Room room = new Room("Living Room");
    Item sofa = new Item("Sofa", new BigDecimal("400.00"), null, null);
    RoomItem roomItem = new RoomItem(room, sofa, 1);
    room.getRoomItems().add(roomItem);
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

    roomService.removeItemFromRoom(1L, sofa.getId());

    assertThat(room.getRoomItems()).isEmpty();
  }

  @Test
  void addPackToRoom_existingRoomAndPack_savesRoomPack() {
    Room room = new Room("Living Room");
    Pack pack = new Pack("Furniture Pack");
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
    when(packRepository.findById(1L)).thenReturn(Optional.of(pack));
    when(roomPackRepository.save(any(RoomPack.class))).thenReturn(new RoomPack(room, pack, 1));
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

    roomService.addPackToRoom(1L, new com.ms.stagerschemes.dto.RoomPackRequest(1L, 1));

    verify(roomPackRepository).save(any(RoomPack.class));
  }

  @Test
  void removePackFromRoom_packInRoom_removesRoomPackFromCollection() {
    Room room = new Room("Living Room");
    Pack pack = new Pack("Furniture Pack");
    RoomPack roomPack = new RoomPack(room, pack, 1);
    room.getRoomPacks().add(roomPack);
    when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

    roomService.removePackFromRoom(1L, pack.getId());

    assertThat(room.getRoomPacks()).isEmpty();
  }
}
