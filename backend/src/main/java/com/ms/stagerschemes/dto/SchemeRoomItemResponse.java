package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.SchemeRoomItem;
import java.math.BigDecimal;

public record SchemeRoomItemResponse(
    Long id,
    Long itemId,
    String itemName,
    BigDecimal itemPrice,
    int quantity,
    BigDecimal lineTotal) {

  public static SchemeRoomItemResponse from(SchemeRoomItem schemeRoomItem) {
    BigDecimal lineTotal =
        schemeRoomItem.getItem().getPrice().multiply(BigDecimal.valueOf(schemeRoomItem.getQuantity()));
    return new SchemeRoomItemResponse(
        schemeRoomItem.getId(),
        schemeRoomItem.getItem().getId(),
        schemeRoomItem.getItem().getName(),
        schemeRoomItem.getItem().getPrice(),
        schemeRoomItem.getQuantity(),
        lineTotal);
  }
}
