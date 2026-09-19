package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Item;
import java.math.BigDecimal;

public record ItemResponse(Long id, String name, BigDecimal price, String webLink, String category, boolean hasImage) {

  public static ItemResponse from(Item item, boolean hasImage) {
    return new ItemResponse(item.getId(), item.getName(), item.getPrice(), item.getWebLink(), item.getCategory(), hasImage);
  }
}
