package com.ms.stagerschemes.dto;

import java.math.BigDecimal;

public record SchemeResponse(Long id, String name, BigDecimal totalPrice, boolean template) {}
