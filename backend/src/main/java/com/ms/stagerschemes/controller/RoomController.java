package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.RoomApi;
import com.ms.stagerschemes.dto.RoomItemRequest;
import com.ms.stagerschemes.dto.RoomPackRequest;
import com.ms.stagerschemes.dto.RoomRequest;
import com.ms.stagerschemes.dto.RoomResponse;
import com.ms.stagerschemes.service.RoomService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController implements RoomApi {

  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @Override
  public ResponseEntity<List<RoomResponse>> findAllRooms() {
    return ResponseEntity.ok(roomService.findAllRooms());
  }

  @Override
  public ResponseEntity<RoomResponse> findRoomById(Long roomId) {
    return ResponseEntity.ok(roomService.findRoomById(roomId));
  }

  @Override
  public ResponseEntity<RoomResponse> createRoom(RoomRequest roomRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(roomRequest));
  }

  @Override
  public ResponseEntity<RoomResponse> updateRoom(Long roomId, RoomRequest roomRequest) {
    return ResponseEntity.ok(roomService.updateRoom(roomId, roomRequest));
  }

  @Override
  public ResponseEntity<Void> deleteRoom(Long roomId) {
    roomService.deleteRoom(roomId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<RoomResponse> addItemToRoom(Long roomId, RoomItemRequest roomItemRequest) {
    return ResponseEntity.ok(roomService.addItemToRoom(roomId, roomItemRequest));
  }

  @Override
  public ResponseEntity<Void> removeItemFromRoom(Long roomId, Long itemId) {
    roomService.removeItemFromRoom(roomId, itemId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<RoomResponse> addPackToRoom(Long roomId, RoomPackRequest roomPackRequest) {
    return ResponseEntity.ok(roomService.addPackToRoom(roomId, roomPackRequest));
  }

  @Override
  public ResponseEntity<Void> removePackFromRoom(Long roomId, Long packId) {
    roomService.removePackFromRoom(roomId, packId);
    return ResponseEntity.noContent().build();
  }
}
