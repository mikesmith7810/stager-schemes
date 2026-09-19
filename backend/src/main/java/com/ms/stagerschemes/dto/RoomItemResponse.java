package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.RoomItem;
import java.math.BigDecimal;

public record RoomItemResponse(Long itemId, String itemName, BigDecimal itemPrice, int quantity) {

  public static RoomItemResponse from(RoomItem roomItem) {
    return new RoomItemResponse(
        roomItem.getItem().getId(),
        roomItem.getItem().getName(),
        roomItem.getItem().getPrice(),
        roomItem.getQuantity());
  }
}
