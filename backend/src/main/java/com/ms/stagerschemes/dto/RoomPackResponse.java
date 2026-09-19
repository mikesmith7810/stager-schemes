package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.RoomPack;
import java.math.BigDecimal;
import java.util.List;

public record RoomPackResponse(
    Long packId, String packName, int quantity, List<PackItemResponse> items, BigDecimal packTotal) {

  public static RoomPackResponse from(RoomPack roomPack) {
    List<PackItemResponse> items =
        roomPack.getPack().getPackItems().stream().map(PackItemResponse::from).toList();
    BigDecimal packTotal =
        items.stream()
            .map(pi -> pi.itemPrice().multiply(BigDecimal.valueOf(pi.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new RoomPackResponse(
        roomPack.getPack().getId(),
        roomPack.getPack().getName(),
        roomPack.getQuantity(),
        items,
        packTotal);
  }
}
