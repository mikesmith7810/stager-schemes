package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.ItemImage;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

  @Query("SELECT ii.id FROM ItemImage ii WHERE ii.id IN :ids")
  Set<Long> findExistingIds(@Param("ids") Collection<Long> ids);
}
