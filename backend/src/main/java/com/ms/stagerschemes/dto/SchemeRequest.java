package com.ms.stagerschemes.dto;

import java.math.BigDecimal;

public record SchemeRequest(
    String name, BigDecimal transportCost, BigDecimal stagingCost, BigDecimal designCost) {}
