package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.PackItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackItemRepository extends JpaRepository<PackItem, Long> {

  List<PackItem> findByItemId(Long itemId);
}
