package com.ms.stagerschemes.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scheme_room")
public class SchemeRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scheme_id", nullable = false)
  private Scheme scheme;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "schemeRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SchemeRoomItem> schemeRoomItems = new ArrayList<>();

  @OneToMany(mappedBy = "schemeRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SchemeRoomPack> schemeRoomPacks = new ArrayList<>();

  protected SchemeRoom() {}

  public SchemeRoom(Scheme scheme, Room room, String name) {
    this.scheme = scheme;
    this.room = room;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public Scheme getScheme() {
    return scheme;
  }

  public Room getRoom() {
    return room;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SchemeRoomItem> getSchemeRoomItems() {
    return schemeRoomItems;
  }

  public List<SchemeRoomPack> getSchemeRoomPacks() {
    return schemeRoomPacks;
  }
}
