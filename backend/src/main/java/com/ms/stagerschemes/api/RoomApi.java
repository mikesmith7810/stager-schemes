package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.RoomItemRequest;
import com.ms.stagerschemes.dto.RoomPackRequest;
import com.ms.stagerschemes.dto.RoomRequest;
import com.ms.stagerschemes.dto.RoomResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/rooms")
public interface RoomApi {

  @GetMapping
  ResponseEntity<List<RoomResponse>> findAllRooms();

  @GetMapping("/{roomId}")
  ResponseEntity<RoomResponse> findRoomById(@PathVariable Long roomId);

  @PostMapping
  ResponseEntity<RoomResponse> createRoom(@RequestBody RoomRequest roomRequest);

  @PutMapping("/{roomId}")
  ResponseEntity<RoomResponse> updateRoom(
      @PathVariable Long roomId, @RequestBody RoomRequest roomRequest);

  @DeleteMapping("/{roomId}")
  ResponseEntity<Void> deleteRoom(@PathVariable Long roomId);

  @PostMapping("/{roomId}/items")
  ResponseEntity<RoomResponse> addItemToRoom(
      @PathVariable Long roomId, @RequestBody RoomItemRequest roomItemRequest);

  @DeleteMapping("/{roomId}/items/{itemId}")
  ResponseEntity<Void> removeItemFromRoom(@PathVariable Long roomId, @PathVariable Long itemId);

  @PostMapping("/{roomId}/packs")
  ResponseEntity<RoomResponse> addPackToRoom(
      @PathVariable Long roomId, @RequestBody RoomPackRequest roomPackRequest);

  @DeleteMapping("/{roomId}/packs/{packId}")
  ResponseEntity<Void> removePackFromRoom(@PathVariable Long roomId, @PathVariable Long packId);
}
