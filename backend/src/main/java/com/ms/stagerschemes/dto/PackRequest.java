package com.ms.stagerschemes.dto;

import java.util.List;

public record PackRequest(String name, List<PackItemRequest> items) {}
