package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Room;
import java.util.List;

public record RoomResponse(
    Long id, String name, List<RoomItemResponse> items, List<RoomPackResponse> packs) {

  public static RoomResponse from(Room room) {
    List<RoomItemResponse> roomItemResponses =
        room.getRoomItems().stream().map(RoomItemResponse::from).toList();
    List<RoomPackResponse> roomPackResponses =
        room.getRoomPacks().stream().map(RoomPackResponse::from).toList();
    return new RoomResponse(room.getId(), room.getName(), roomItemResponses, roomPackResponses);
  }
}
