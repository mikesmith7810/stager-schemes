package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.RoomItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomItemRepository extends JpaRepository<RoomItem, Long> {

  List<RoomItem> findByItemId(Long itemId);
}
