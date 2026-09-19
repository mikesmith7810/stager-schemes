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
@Table(name = "scheme_room_item")
public class SchemeRoomItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scheme_room_id", nullable = false)
  private SchemeRoom schemeRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @Column(nullable = false)
  private int quantity;

  protected SchemeRoomItem() {}

  public SchemeRoomItem(SchemeRoom schemeRoom, Item item, int quantity) {
    this.schemeRoom = schemeRoom;
    this.item = item;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public SchemeRoom getSchemeRoom() {
    return schemeRoom;
  }

  public Item getItem() {
    return item;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
