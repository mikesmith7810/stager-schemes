package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.RoomItemRequest;
import com.ms.stagerschemes.dto.RoomPackRequest;
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
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

  private final RoomRepository roomRepository;
  private final RoomItemRepository roomItemRepository;
  private final RoomPackRepository roomPackRepository;
  private final ItemRepository itemRepository;
  private final PackRepository packRepository;

  public RoomService(
      RoomRepository roomRepository,
      RoomItemRepository roomItemRepository,
      RoomPackRepository roomPackRepository,
      ItemRepository itemRepository,
      PackRepository packRepository) {
    this.roomRepository = roomRepository;
    this.roomItemRepository = roomItemRepository;
    this.roomPackRepository = roomPackRepository;
    this.itemRepository = itemRepository;
    this.packRepository = packRepository;
  }

  @Transactional(readOnly = true)
  public List<RoomResponse> findAllRooms() {
    return roomRepository.findAll().stream().map(RoomResponse::from).toList();
  }

  @Transactional(readOnly = true)
  public RoomResponse findRoomById(Long roomId) {
    return roomRepository
        .findById(roomId)
        .map(RoomResponse::from)
        .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
  }

  @Transactional
  public RoomResponse createRoom(RoomRequest roomRequest) {
    Room savedRoom = roomRepository.save(new Room(roomRequest.name()));
    if (roomRequest.items() != null) {
      roomRequest.items().forEach(itemRequest -> addItemToRoomInternal(savedRoom, itemRequest));
    }
    if (roomRequest.packs() != null) {
      roomRequest.packs().forEach(packRequest -> addPackToRoomInternal(savedRoom, packRequest));
    }
    return RoomResponse.from(roomRepository.findById(savedRoom.getId()).orElseThrow());
  }

  @Transactional
  public RoomResponse updateRoom(Long roomId, RoomRequest roomRequest) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    room.setName(roomRequest.name());
    return RoomResponse.from(roomRepository.save(room));
  }

  @Transactional
  public void deleteRoom(Long roomId) {
    if (!roomRepository.existsById(roomId)) {
      throw new NoSuchElementException("Room not found: " + roomId);
    }
    roomRepository.deleteById(roomId);
  }

  @Transactional
  public RoomResponse addItemToRoom(Long roomId, RoomItemRequest roomItemRequest) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    addItemToRoomInternal(room, roomItemRequest);
    return RoomResponse.from(roomRepository.findById(roomId).orElseThrow());
  }

  @Transactional
  public void removeItemFromRoom(Long roomId, Long itemId) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    RoomItem roomItem =
        room.getRoomItems().stream()
            .filter(ri -> ri.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Item not in room: " + itemId));
    room.getRoomItems().remove(roomItem);
  }

  @Transactional
  public RoomResponse addPackToRoom(Long roomId, RoomPackRequest roomPackRequest) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    addPackToRoomInternal(room, roomPackRequest);
    return RoomResponse.from(roomRepository.findById(roomId).orElseThrow());
  }

  @Transactional
  public void removePackFromRoom(Long roomId, Long packId) {
    Room room =
        roomRepository
            .findById(roomId)
            .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    RoomPack roomPack =
        room.getRoomPacks().stream()
            .filter(rp -> rp.getPack().getId().equals(packId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Pack not in room: " + packId));
    room.getRoomPacks().remove(roomPack);
  }

  private void addItemToRoomInternal(Room room, RoomItemRequest roomItemRequest) {
    Item item =
        itemRepository
            .findById(roomItemRequest.itemId())
            .orElseThrow(
                () -> new NoSuchElementException("Item not found: " + roomItemRequest.itemId()));
    roomItemRepository.save(new RoomItem(room, item, roomItemRequest.quantity()));
  }

  private void addPackToRoomInternal(Room room, RoomPackRequest roomPackRequest) {
    Pack pack =
        packRepository
            .findById(roomPackRequest.packId())
            .orElseThrow(
                () -> new NoSuchElementException("Pack not found: " + roomPackRequest.packId()));
    roomPackRepository.save(new RoomPack(room, pack, roomPackRequest.quantity()));
  }
}
