package com.ms.stagerschemes.repository;

import com.ms.stagerschemes.model.SchemeRoomPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchemeRoomPackRepository extends JpaRepository<SchemeRoomPack, Long> {

  @Modifying
  @Query("DELETE FROM SchemeRoomPack srp WHERE srp.pack.id = :packId")
  void deleteAllByPackId(@Param("packId") Long packId);
}
