package com.ms.stagerschemes.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.Room;
import com.ms.stagerschemes.model.RoomItem;
import com.ms.stagerschemes.model.RoomPack;
import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.model.SchemeRoomItem;
import com.ms.stagerschemes.model.SchemeRoomPack;
import com.ms.stagerschemes.repository.SchemeRoomItemRepository;
import com.ms.stagerschemes.repository.SchemeRoomPackRepository;
import com.ms.stagerschemes.repository.SchemeRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchemeRoomCopierTest {

  @Mock private SchemeRoomRepository schemeRoomRepository;
  @Mock private SchemeRoomItemRepository schemeRoomItemRepository;
  @Mock private SchemeRoomPackRepository schemeRoomPackRepository;

  private SchemeRoomCopier schemeRoomCopier;

  @BeforeEach
  void setUp() {
    schemeRoomCopier =
        new SchemeRoomCopier(schemeRoomRepository, schemeRoomItemRepository, schemeRoomPackRepository);
  }

  @Test
  void copyRoomToScheme_createsSchemeRoomWithRoomName() {
    Scheme scheme = new Scheme("Test Scheme");
    Room room = new Room("Master Bedroom");
    SchemeRoom savedSchemeRoom = new SchemeRoom(scheme, room, room.getName());
    when(schemeRoomRepository.save(any(SchemeRoom.class))).thenReturn(savedSchemeRoom);

    SchemeRoom result = schemeRoomCopier.copyRoomToScheme(scheme, room);

    assertThat(result.getName()).isEqualTo("Master Bedroom");
    verify(schemeRoomRepository).save(any(SchemeRoom.class));
  }

  @Test
  void copyRoomToScheme_copiesAllRoomItemsAsSchemeRoomItems() {
    Scheme scheme = new Scheme("Test Scheme");
    Room room = new Room("Living Room");
    Item chair = new Item("Chair", new java.math.BigDecimal("100.00"), null);
    Item lamp = new Item("Lamp", new java.math.BigDecimal("50.00"), null);
    room.getRoomItems().add(new RoomItem(room, chair, 2));
    room.getRoomItems().add(new RoomItem(room, lamp, 1));

    SchemeRoom savedSchemeRoom = new SchemeRoom(scheme, room, room.getName());
    when(schemeRoomRepository.save(any(SchemeRoom.class))).thenReturn(savedSchemeRoom);
    when(schemeRoomItemRepository.save(any(SchemeRoomItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    schemeRoomCopier.copyRoomToScheme(scheme, room);

    verify(schemeRoomItemRepository, times(2)).save(any(SchemeRoomItem.class));
  }

  @Test
  void copyRoomToScheme_copiesAllRoomPacksAsSchemeRoomPacks() {
    Scheme scheme = new Scheme("Test Scheme");
    Room room = new Room("Bedroom");
    Pack furniturePack = new Pack("Furniture Pack");
    room.getRoomPacks().add(new RoomPack(room, furniturePack, 1));

    SchemeRoom savedSchemeRoom = new SchemeRoom(scheme, room, room.getName());
    when(schemeRoomRepository.save(any(SchemeRoom.class))).thenReturn(savedSchemeRoom);
    when(schemeRoomPackRepository.save(any(SchemeRoomPack.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    schemeRoomCopier.copyRoomToScheme(scheme, room);

    verify(schemeRoomPackRepository, times(1)).save(any(SchemeRoomPack.class));
  }

  @Test
  void copyRoomToScheme_roomWithNoItemsOrPacks_savesOnlySchemeRoom() {
    Scheme scheme = new Scheme("Test Scheme");
    Room room = new Room("Hallway");
    SchemeRoom savedSchemeRoom = new SchemeRoom(scheme, room, room.getName());
    when(schemeRoomRepository.save(any(SchemeRoom.class))).thenReturn(savedSchemeRoom);

    schemeRoomCopier.copyRoomToScheme(scheme, room);

    verify(schemeRoomRepository).save(any(SchemeRoom.class));
    verify(schemeRoomItemRepository, times(0)).save(any());
    verify(schemeRoomPackRepository, times(0)).save(any());
  }
}
