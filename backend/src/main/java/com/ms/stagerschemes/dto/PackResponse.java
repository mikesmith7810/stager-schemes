package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Pack;
import java.util.List;

public record PackResponse(Long id, String name, List<PackItemResponse> items) {

  public static PackResponse from(Pack pack) {
    List<PackItemResponse> packItemResponses =
        pack.getPackItems().stream().map(PackItemResponse::from).toList();
    return new PackResponse(pack.getId(), pack.getName(), packItemResponses);
  }
}
