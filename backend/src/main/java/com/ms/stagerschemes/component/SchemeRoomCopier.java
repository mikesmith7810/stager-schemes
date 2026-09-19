package com.ms.stagerschemes.component;

import com.ms.stagerschemes.model.Room;
import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.model.SchemeRoomItem;
import com.ms.stagerschemes.model.SchemeRoomPack;
import com.ms.stagerschemes.repository.SchemeRoomItemRepository;
import com.ms.stagerschemes.repository.SchemeRoomPackRepository;
import com.ms.stagerschemes.repository.SchemeRoomRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SchemeRoomCopier {

  private final SchemeRoomRepository schemeRoomRepository;
  private final SchemeRoomItemRepository schemeRoomItemRepository;
  private final SchemeRoomPackRepository schemeRoomPackRepository;

  public SchemeRoomCopier(
      SchemeRoomRepository schemeRoomRepository,
      SchemeRoomItemRepository schemeRoomItemRepository,
      SchemeRoomPackRepository schemeRoomPackRepository) {
    this.schemeRoomRepository = schemeRoomRepository;
    this.schemeRoomItemRepository = schemeRoomItemRepository;
    this.schemeRoomPackRepository = schemeRoomPackRepository;
  }

  @Transactional
  public SchemeRoom copyRoomToScheme(Scheme scheme, Room room) {
    SchemeRoom schemeRoom =
        schemeRoomRepository.save(new SchemeRoom(scheme, room, room.getName()));

    room.getRoomItems()
        .forEach(
            roomItem ->
                schemeRoomItemRepository.save(
                    new SchemeRoomItem(schemeRoom, roomItem.getItem(), roomItem.getQuantity())));

    room.getRoomPacks()
        .forEach(
            roomPack ->
                schemeRoomPackRepository.save(
                    new SchemeRoomPack(schemeRoom, roomPack.getPack(), roomPack.getQuantity())));

    return schemeRoom;
  }
}
