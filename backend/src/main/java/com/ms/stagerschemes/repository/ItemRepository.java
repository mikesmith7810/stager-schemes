package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findAllByDeletedFalse();

  List<Item> findAllByDeletedTrue();
}
