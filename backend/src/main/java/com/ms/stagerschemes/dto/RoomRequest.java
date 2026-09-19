package com.ms.stagerschemes.dto;

import java.util.List;

public record RoomRequest(String name, List<RoomItemRequest> items, List<RoomPackRequest> packs) {}
