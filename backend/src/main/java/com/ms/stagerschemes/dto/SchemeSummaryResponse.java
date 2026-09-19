package com.ms.stagerschemes.dto;

import java.math.BigDecimal;
import java.util.List;

public record SchemeSummaryResponse(
    Long id,
    String name,
    List<SchemeRoomSummary> rooms,
    BigDecimal totalPrice,
    BigDecimal transportCost,
    BigDecimal stagingCost,
    BigDecimal designCost,
    String customerSummaryOverrides) {}
