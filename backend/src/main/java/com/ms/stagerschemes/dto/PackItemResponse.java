package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.PackItem;
import java.math.BigDecimal;

public record PackItemResponse(Long itemId, String itemName, BigDecimal itemPrice, int quantity) {

  public static PackItemResponse from(PackItem packItem) {
    return new PackItemResponse(
        packItem.getItem().getId(),
        packItem.getItem().getName(),
        packItem.getItem().getPrice(),
        packItem.getQuantity());
  }
}
