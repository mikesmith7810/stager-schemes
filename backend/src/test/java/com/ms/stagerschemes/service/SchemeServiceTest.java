package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.component.SchemePriceCalculator;
import com.ms.stagerschemes.component.SchemeRoomCopier;
import com.ms.stagerschemes.dto.AddRoomToSchemeRequest;
import com.ms.stagerschemes.dto.SchemeRequest;
import com.ms.stagerschemes.dto.SchemeResponse;
import com.ms.stagerschemes.dto.SchemeSummaryResponse;
import com.ms.stagerschemes.model.Room;
import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.repository.ItemRepository;
import com.ms.stagerschemes.repository.PackRepository;
import com.ms.stagerschemes.repository.RoomRepository;
import com.ms.stagerschemes.repository.SchemeRepository;
import com.ms.stagerschemes.repository.SchemeRoomItemRepository;
import com.ms.stagerschemes.repository.SchemeRoomPackRepository;
import com.ms.stagerschemes.repository.SchemeRoomRepository;
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
class SchemeServiceTest {

  @Mock private SchemeRepository schemeRepository;
  @Mock private SchemeRoomRepository schemeRoomRepository;
  @Mock private SchemeRoomItemRepository schemeRoomItemRepository;
  @Mock private SchemeRoomPackRepository schemeRoomPackRepository;
  @Mock private RoomRepository roomRepository;
  @Mock private ItemRepository itemRepository;
  @Mock private PackRepository packRepository;
  @Mock private SchemePriceCalculator schemePriceCalculator;
  @Mock private SchemeRoomCopier schemeRoomCopier;

  private SchemeService schemeService;

  @BeforeEach
  void setUp() {
    schemeService =
        new SchemeService(
            schemeRepository,
            schemeRoomRepository,
            schemeRoomItemRepository,
            schemeRoomPackRepository,
            roomRepository,
            itemRepository,
            packRepository,
            schemePriceCalculator,
            schemeRoomCopier);
  }

  @Test
  void findAllSchemes_returnsAllSchemesWithCalculatedPrices() {
    Scheme scheme = new Scheme("Luxury Flat");
    when(schemeRepository.findAll()).thenReturn(List.of(scheme));
    when(schemePriceCalculator.calculateTotalPrice(scheme)).thenReturn(new BigDecimal("1500.00"));

    List<SchemeResponse> result = schemeService.findAllSchemes();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Luxury Flat");
    assertThat(result.get(0).totalPrice()).isEqualByComparingTo("1500.00");
  }

  @Test
  void findSchemeById_existingScheme_returnsSchemeWithCalculatedPrice() {
    Scheme scheme = new Scheme("Studio Flat");
    when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
    when(schemePriceCalculator.calculateTotalPrice(scheme)).thenReturn(new BigDecimal("800.00"));

    SchemeResponse result = schemeService.findSchemeById(1L);

    assertThat(result.name()).isEqualTo("Studio Flat");
    assertThat(result.totalPrice()).isEqualByComparingTo("800.00");
  }

  @Test
  void findSchemeById_nonExistingScheme_throwsNoSuchElementException() {
    when(schemeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> schemeService.findSchemeById(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }

  @Test
  void createScheme_savesAndReturnsNewSchemeWithZeroPrice() {
    SchemeRequest schemeRequest = new SchemeRequest("New Scheme");
    Scheme savedScheme = new Scheme("New Scheme");
    when(schemeRepository.save(any(Scheme.class))).thenReturn(savedScheme);

    SchemeResponse result = schemeService.createScheme(schemeRequest);

    assertThat(result.name()).isEqualTo("New Scheme");
    assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    verify(schemeRepository).save(any(Scheme.class));
  }

  @Test
  void updateScheme_existingScheme_updatesNameAndReturnsUpdatedScheme() {
    Scheme existingScheme = new Scheme("Old Name");
    when(schemeRepository.findById(1L)).thenReturn(Optional.of(existingScheme));
    when(schemeRepository.save(any(Scheme.class))).thenReturn(existingScheme);
    when(schemePriceCalculator.calculateTotalPrice(any())).thenReturn(BigDecimal.ZERO);

    SchemeResponse result = schemeService.updateScheme(1L, new SchemeRequest("New Name"));

    assertThat(result.name()).isEqualTo("New Name");
  }

  @Test
  void deleteScheme_existingScheme_deletesSuccessfully() {
    when(schemeRepository.existsById(1L)).thenReturn(true);

    schemeService.deleteScheme(1L);

    verify(schemeRepository).deleteById(1L);
  }

  @Test
  void deleteScheme_nonExistingScheme_throwsNoSuchElementException() {
    when(schemeRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> schemeService.deleteScheme(99L))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void getSchemeSummary_existingScheme_returnsSummaryWithRoomsAndTotalPrice() {
    Scheme scheme = new Scheme("Test Scheme");
    when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
    when(schemePriceCalculator.calculateTotalPrice(scheme)).thenReturn(new BigDecimal("500.00"));

    SchemeSummaryResponse result = schemeService.getSchemeSummary(1L);

    assertThat(result.name()).isEqualTo("Test Scheme");
    assertThat(result.totalPrice()).isEqualByComparingTo("500.00");
    assertThat(result.rooms()).isEmpty();
  }

  @Test
  void addRoomToScheme_existingSchemeAndRoom_delegatesToCopierAndReturnsRoomSummary() {
    Scheme scheme = new Scheme("Scheme");
    Room room = new Room("Living Room");
    SchemeRoom copiedSchemeRoom = new SchemeRoom(scheme, room, "Living Room");
    when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
    when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
    when(schemeRoomCopier.copyRoomToScheme(scheme, room)).thenReturn(copiedSchemeRoom);
    when(schemePriceCalculator.calculateSchemeRoomPrice(copiedSchemeRoom))
        .thenReturn(BigDecimal.ZERO);

    schemeService.addRoomToScheme(1L, new AddRoomToSchemeRequest(2L));

    verify(schemeRoomCopier).copyRoomToScheme(scheme, room);
  }

  @Test
  void removeRoomFromScheme_schemeRoomBelongsToScheme_deletesSchemeRoom() {
    Scheme scheme = new Scheme("Scheme");
    Room room = new Room("Bedroom");
    SchemeRoom schemeRoom = new SchemeRoom(scheme, room, "Bedroom");
    when(schemeRoomRepository.findById(10L)).thenReturn(Optional.of(schemeRoom));

    schemeService.removeRoomFromScheme(scheme.getId(), 10L);

    verify(schemeRoomRepository).delete(schemeRoom);
  }
}
