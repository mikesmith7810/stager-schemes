package com.ms.stagerschemes.dto;

import java.math.BigDecimal;
import java.util.List;

public record SchemeRoomSummary(
    Long id,
    Long roomId,
    String name,
    List<SchemeRoomItemResponse> items,
    List<SchemeRoomPackResponse> packs,
    BigDecimal roomTotal) {}
