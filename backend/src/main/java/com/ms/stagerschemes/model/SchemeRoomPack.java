package com.ms.stagerschemes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "scheme_room_pack")
public class SchemeRoomPack {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scheme_room_id", nullable = false)
  private SchemeRoom schemeRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pack_id", nullable = false)
  private Pack pack;

  @Column(nullable = false)
  private int quantity;

  protected SchemeRoomPack() {}

  public SchemeRoomPack(SchemeRoom schemeRoom, Pack pack, int quantity) {
    this.schemeRoom = schemeRoom;
    this.pack = pack;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public SchemeRoom getSchemeRoom() {
    return schemeRoom;
  }

  public Pack getPack() {
    return pack;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
