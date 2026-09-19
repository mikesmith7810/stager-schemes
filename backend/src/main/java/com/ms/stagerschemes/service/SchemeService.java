package com.ms.stagerschemes.service;

import com.ms.stagerschemes.component.SchemePriceCalculator;
import com.ms.stagerschemes.component.SchemeRoomCopier;
import com.ms.stagerschemes.dto.AddItemToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddPackToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddRoomToSchemeRequest;
import com.ms.stagerschemes.dto.SchemeRequest;
import com.ms.stagerschemes.dto.SchemeResponse;
import com.ms.stagerschemes.dto.SchemeRoomItemResponse;
import com.ms.stagerschemes.dto.SchemeRoomPackResponse;
import com.ms.stagerschemes.dto.SchemeRoomSummary;
import com.ms.stagerschemes.dto.SchemeSummaryResponse;
import com.ms.stagerschemes.model.Item;
import com.ms.stagerschemes.model.Pack;
import com.ms.stagerschemes.model.Room;
import com.ms.stagerschemes.model.Scheme;
import com.ms.stagerschemes.model.SchemeRoom;
import com.ms.stagerschemes.model.SchemeRoomItem;
import com.ms.stagerschemes.model.SchemeRoomPack;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchemeService {

  private final SchemeRepository schemeRepository;
  private final SchemeRoomRepository schemeRoomRepository;
  private final SchemeRoomItemRepository schemeRoomItemRepository;
  private final SchemeRoomPackRepository schemeRoomPackRepository;
  private final RoomRepository roomRepository;
  private final ItemRepository itemRepository;
  private final PackRepository packRepository;
  private final SchemePriceCalculator schemePriceCalculator;
  private final SchemeRoomCopier schemeRoomCopier;

  public SchemeService(
      SchemeRepository schemeRepository,
      SchemeRoomRepository schemeRoomRepository,
      SchemeRoomItemRepository schemeRoomItemRepository,
      SchemeRoomPackRepository schemeRoomPackRepository,
      RoomRepository roomRepository,
      ItemRepository itemRepository,
      PackRepository packRepository,
      SchemePriceCalculator schemePriceCalculator,
      SchemeRoomCopier schemeRoomCopier) {
    this.schemeRepository = schemeRepository;
    this.schemeRoomRepository = schemeRoomRepository;
    this.schemeRoomItemRepository = schemeRoomItemRepository;
    this.schemeRoomPackRepository = schemeRoomPackRepository;
    this.roomRepository = roomRepository;
    this.itemRepository = itemRepository;
    this.packRepository = packRepository;
    this.schemePriceCalculator = schemePriceCalculator;
    this.schemeRoomCopier = schemeRoomCopier;
  }

  @Transactional(readOnly = true)
  public List<SchemeResponse> findAllSchemes() {
    return schemeRepository.findAll().stream()
        .map(
            scheme ->
                new SchemeResponse(
                    scheme.getId(),
                    scheme.getName(),
                    schemePriceCalculator.calculateTotalPrice(scheme),
                    scheme.isTemplate()))
        .toList();
  }

  @Transactional(readOnly = true)
  public SchemeResponse findSchemeById(Long schemeId) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    return new SchemeResponse(
        scheme.getId(),
        scheme.getName(),
        schemePriceCalculator.calculateTotalPrice(scheme),
        scheme.isTemplate());
  }

  @Transactional
  public SchemeResponse createScheme(SchemeRequest schemeRequest) {
    Scheme scheme = new Scheme(schemeRequest.name());
    scheme.setTransportCost(nullSafeZero(schemeRequest.transportCost()));
    scheme.setStagingCost(nullSafeZero(schemeRequest.stagingCost()));
    scheme.setDesignCost(nullSafeZero(schemeRequest.designCost()));
    Scheme savedScheme = schemeRepository.save(scheme);
    return new SchemeResponse(savedScheme.getId(), savedScheme.getName(), BigDecimal.ZERO, false);
  }

  @Transactional
  public SchemeResponse updateScheme(Long schemeId, SchemeRequest schemeRequest) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    scheme.setName(schemeRequest.name());
    scheme.setTransportCost(nullSafeZero(schemeRequest.transportCost()));
    scheme.setStagingCost(nullSafeZero(schemeRequest.stagingCost()));
    scheme.setDesignCost(nullSafeZero(schemeRequest.designCost()));
    Scheme savedScheme = schemeRepository.save(scheme);
    return new SchemeResponse(
        savedScheme.getId(),
        savedScheme.getName(),
        schemePriceCalculator.calculateTotalPrice(savedScheme),
        savedScheme.isTemplate());
  }

  @Transactional
  public void deleteScheme(Long schemeId) {
    if (!schemeRepository.existsById(schemeId)) {
      throw new NoSuchElementException("Scheme not found: " + schemeId);
    }
    schemeRepository.deleteById(schemeId);
  }

  @Transactional(readOnly = true)
  public SchemeSummaryResponse getSchemeSummary(Long schemeId) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));

    List<SchemeRoomSummary> roomSummaries =
        scheme.getSchemeRooms().stream().map(this::toSchemeRoomSummary).toList();

    BigDecimal totalPrice = schemePriceCalculator.calculateTotalPrice(scheme);
    return new SchemeSummaryResponse(
        scheme.getId(),
        scheme.getName(),
        roomSummaries,
        totalPrice,
        scheme.getTransportCost(),
        scheme.getStagingCost(),
        scheme.getDesignCost(),
        scheme.getCustomerSummaryOverrides());
  }

  @Transactional
  public void saveCustomerSummaryOverrides(Long schemeId, String overrides) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    scheme.setCustomerSummaryOverrides(overrides);
    schemeRepository.save(scheme);
  }

  @Transactional
  public SchemeRoomSummary addRoomToScheme(Long schemeId, AddRoomToSchemeRequest addRoomToSchemeRequest) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    Room room =
        roomRepository
            .findById(addRoomToSchemeRequest.roomId())
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Room not found: " + addRoomToSchemeRequest.roomId()));
    SchemeRoom schemeRoom = schemeRoomCopier.copyRoomToScheme(scheme, room);
    return toSchemeRoomSummary(schemeRoom);
  }

  @Transactional
  public void removeRoomFromScheme(Long schemeId, Long schemeRoomId) {
    SchemeRoom schemeRoom =
        schemeRoomRepository
            .findById(schemeRoomId)
            .orElseThrow(() -> new NoSuchElementException("SchemeRoom not found: " + schemeRoomId));
    if (!schemeRoom.getScheme().getId().equals(schemeId)) {
      throw new IllegalArgumentException(
          "SchemeRoom " + schemeRoomId + " does not belong to scheme " + schemeId);
    }
    schemeRoomRepository.delete(schemeRoom);
  }

  @Transactional
  public SchemeRoomSummary addItemToSchemeRoom(
      Long schemeRoomId, AddItemToSchemeRoomRequest addItemToSchemeRoomRequest) {
    SchemeRoom schemeRoom =
        schemeRoomRepository
            .findById(schemeRoomId)
            .orElseThrow(() -> new NoSuchElementException("SchemeRoom not found: " + schemeRoomId));
    Item item =
        itemRepository
            .findById(addItemToSchemeRoomRequest.itemId())
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Item not found: " + addItemToSchemeRoomRequest.itemId()));
    schemeRoomItemRepository.save(
        new SchemeRoomItem(schemeRoom, item, addItemToSchemeRoomRequest.quantity()));
    return toSchemeRoomSummary(schemeRoomRepository.findById(schemeRoomId).orElseThrow());
  }

  @Transactional
  public void removeItemFromSchemeRoom(Long schemeRoomId, Long schemeRoomItemId) {
    if (!schemeRoomItemRepository.existsById(schemeRoomItemId)) {
      throw new NoSuchElementException("SchemeRoomItem not found: " + schemeRoomItemId);
    }
    schemeRoomItemRepository.deleteById(schemeRoomItemId);
  }

  @Transactional
  public SchemeRoomSummary addPackToSchemeRoom(
      Long schemeRoomId, AddPackToSchemeRoomRequest addPackToSchemeRoomRequest) {
    SchemeRoom schemeRoom =
        schemeRoomRepository
            .findById(schemeRoomId)
            .orElseThrow(() -> new NoSuchElementException("SchemeRoom not found: " + schemeRoomId));
    Pack pack =
        packRepository
            .findById(addPackToSchemeRoomRequest.packId())
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Pack not found: " + addPackToSchemeRoomRequest.packId()));
    schemeRoomPackRepository.save(
        new SchemeRoomPack(schemeRoom, pack, addPackToSchemeRoomRequest.quantity()));
    return toSchemeRoomSummary(schemeRoomRepository.findById(schemeRoomId).orElseThrow());
  }

  @Transactional
  public void removePackFromSchemeRoom(Long schemeRoomId, Long schemeRoomPackId) {
    if (!schemeRoomPackRepository.existsById(schemeRoomPackId)) {
      throw new NoSuchElementException("SchemeRoomPack not found: " + schemeRoomPackId);
    }
    schemeRoomPackRepository.deleteById(schemeRoomPackId);
  }

  @Transactional
  public SchemeResponse setTemplate(Long schemeId, boolean template) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    scheme.setTemplate(template);
    Scheme savedScheme = schemeRepository.save(scheme);
    return new SchemeResponse(
        savedScheme.getId(),
        savedScheme.getName(),
        schemePriceCalculator.calculateTotalPrice(savedScheme),
        savedScheme.isTemplate());
  }

  @Transactional
  public SchemeResponse renameScheme(Long schemeId, String name) {
    Scheme scheme =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    scheme.setName(name);
    Scheme savedScheme = schemeRepository.save(scheme);
    return new SchemeResponse(
        savedScheme.getId(),
        savedScheme.getName(),
        schemePriceCalculator.calculateTotalPrice(savedScheme),
        savedScheme.isTemplate());
  }

  @Transactional
  public SchemeResponse duplicateScheme(Long schemeId, String name) {
    Scheme source =
        schemeRepository
            .findById(schemeId)
            .orElseThrow(() -> new NoSuchElementException("Scheme not found: " + schemeId));
    Scheme copy = new Scheme(name);
    copy.setTransportCost(source.getTransportCost());
    copy.setStagingCost(source.getStagingCost());
    copy.setDesignCost(source.getDesignCost());
    copy.setTemplate(false);
    Scheme savedCopy = schemeRepository.save(copy);
    for (SchemeRoom sourceRoom : source.getSchemeRooms()) {
      SchemeRoom newRoom =
          schemeRoomRepository.save(
              new SchemeRoom(savedCopy, sourceRoom.getRoom(), sourceRoom.getName()));
      for (SchemeRoomItem sourceItem : sourceRoom.getSchemeRoomItems()) {
        schemeRoomItemRepository.save(
            new SchemeRoomItem(newRoom, sourceItem.getItem(), sourceItem.getQuantity()));
      }
      for (SchemeRoomPack sourcePack : sourceRoom.getSchemeRoomPacks()) {
        schemeRoomPackRepository.save(
            new SchemeRoomPack(newRoom, sourcePack.getPack(), sourcePack.getQuantity()));
      }
    }
    return new SchemeResponse(savedCopy.getId(), savedCopy.getName(), BigDecimal.ZERO, false);
  }

  private SchemeRoomSummary toSchemeRoomSummary(SchemeRoom schemeRoom) {
    List<SchemeRoomItemResponse> itemResponses =
        schemeRoom.getSchemeRoomItems().stream().map(SchemeRoomItemResponse::from).toList();
    List<SchemeRoomPackResponse> packResponses =
        schemeRoom.getSchemeRoomPacks().stream().map(SchemeRoomPackResponse::from).toList();
    BigDecimal roomTotal = schemePriceCalculator.calculateSchemeRoomPrice(schemeRoom);
    return new SchemeRoomSummary(
        schemeRoom.getId(),
        schemeRoom.getRoom().getId(),
        schemeRoom.getName(),
        itemResponses,
        packResponses,
        roomTotal);
  }

  private BigDecimal nullSafeZero(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
  }
}
