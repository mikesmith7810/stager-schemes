package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.SchemeRoomPack;
import java.math.BigDecimal;
import java.util.List;

public record SchemeRoomPackResponse(
    Long id, Long packId, String packName, List<PackItemResponse> packItems, BigDecimal packTotal) {

  public static SchemeRoomPackResponse from(SchemeRoomPack schemeRoomPack) {
    List<PackItemResponse> packItemResponses =
        schemeRoomPack.getPack().getPackItems().stream().map(PackItemResponse::from).toList();
    BigDecimal packTotal =
        packItemResponses.stream()
            .map(pi -> pi.itemPrice().multiply(BigDecimal.valueOf(pi.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new SchemeRoomPackResponse(
        schemeRoomPack.getId(),
        schemeRoomPack.getPack().getId(),
        schemeRoomPack.getPack().getName(),
        packItemResponses,
        packTotal);
  }
}
