package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.SchemeRoomItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchemeRoomItemRepository extends JpaRepository<SchemeRoomItem, Long> {

  List<SchemeRoomItem> findByItemId(Long itemId);
}
