package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Colour;

public record ColourResponse(Long id, String name) {

  public static ColourResponse from(Colour colour) {
    return new ColourResponse(colour.getId(), colour.getName());
  }
}
