package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.AddItemToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddPackToSchemeRoomRequest;
import com.ms.stagerschemes.dto.AddRoomToSchemeRequest;
import com.ms.stagerschemes.dto.CustomerSummaryOverridesRequest;
import com.ms.stagerschemes.dto.SchemeNameRequest;
import com.ms.stagerschemes.dto.SchemeRequest;
import com.ms.stagerschemes.dto.SchemeResponse;
import com.ms.stagerschemes.dto.SchemeRoomSummary;
import com.ms.stagerschemes.dto.SchemeSummaryResponse;
import com.ms.stagerschemes.dto.SetTemplateRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/schemes")
public interface SchemeApi {

  @GetMapping
  ResponseEntity<List<SchemeResponse>> findAllSchemes();

  @GetMapping("/{schemeId}")
  ResponseEntity<SchemeResponse> findSchemeById(@PathVariable Long schemeId);

  @PostMapping
  ResponseEntity<SchemeResponse> createScheme(@RequestBody SchemeRequest schemeRequest);

  @PutMapping("/{schemeId}")
  ResponseEntity<SchemeResponse> updateScheme(
      @PathVariable Long schemeId, @RequestBody SchemeRequest schemeRequest);

  @DeleteMapping("/{schemeId}")
  ResponseEntity<Void> deleteScheme(@PathVariable Long schemeId);

  @GetMapping("/{schemeId}/summary")
  ResponseEntity<SchemeSummaryResponse> getSchemeSummary(@PathVariable Long schemeId);

  @PostMapping("/{schemeId}/rooms")
  ResponseEntity<SchemeRoomSummary> addRoomToScheme(
      @PathVariable Long schemeId, @RequestBody AddRoomToSchemeRequest addRoomToSchemeRequest);

  @DeleteMapping("/{schemeId}/rooms/{schemeRoomId}")
  ResponseEntity<Void> removeRoomFromScheme(
      @PathVariable Long schemeId, @PathVariable Long schemeRoomId);

  @PostMapping("/{schemeId}/rooms/{schemeRoomId}/items")
  ResponseEntity<SchemeRoomSummary> addItemToSchemeRoom(
      @PathVariable Long schemeId,
      @PathVariable Long schemeRoomId,
      @RequestBody AddItemToSchemeRoomRequest addItemToSchemeRoomRequest);

  @DeleteMapping("/{schemeId}/rooms/{schemeRoomId}/items/{itemId}")
  ResponseEntity<Void> removeItemFromSchemeRoom(
      @PathVariable Long schemeId, @PathVariable Long schemeRoomId, @PathVariable Long itemId);

  @PostMapping("/{schemeId}/rooms/{schemeRoomId}/packs")
  ResponseEntity<SchemeRoomSummary> addPackToSchemeRoom(
      @PathVariable Long schemeId,
      @PathVariable Long schemeRoomId,
      @RequestBody AddPackToSchemeRoomRequest addPackToSchemeRoomRequest);

  @DeleteMapping("/{schemeId}/rooms/{schemeRoomId}/packs/{packId}")
  ResponseEntity<Void> removePackFromSchemeRoom(
      @PathVariable Long schemeId, @PathVariable Long schemeRoomId, @PathVariable Long packId);

  @PutMapping("/{schemeId}/name")
  ResponseEntity<SchemeResponse> renameScheme(
      @PathVariable Long schemeId, @RequestBody SchemeNameRequest schemeNameRequest);

  @PutMapping("/{schemeId}/template")
  ResponseEntity<SchemeResponse> setTemplate(
      @PathVariable Long schemeId, @RequestBody SetTemplateRequest setTemplateRequest);

  @PostMapping("/{schemeId}/duplicate")
  ResponseEntity<SchemeResponse> duplicateScheme(
      @PathVariable Long schemeId, @RequestBody SchemeNameRequest schemeNameRequest);

  @PutMapping("/{schemeId}/customer-summary-overrides")
  ResponseEntity<Void> saveCustomerSummaryOverrides(
      @PathVariable Long schemeId, @RequestBody CustomerSummaryOverridesRequest request);
}
