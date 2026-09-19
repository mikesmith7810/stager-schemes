package com.ms.stagerschemes.dto;

import java.math.BigDecimal;

public record ItemRequest(String name, BigDecimal price, String webLink, String category) {}
