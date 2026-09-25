package com.ms.stagerschemes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "web_link", length = 2048)
  private String webLink;

  @Column(length = 64)
  private String category;

  @Column(length = 255)
  private String supplier;

  @Column(length = 128)
  private String colour;

  @Column(nullable = false)
  private boolean deleted = false;

  protected Item() {}

  public Item(String name, BigDecimal price, String webLink, String category, String supplier, String colour) {
    this.name = name;
    this.price = price;
    this.webLink = webLink;
    this.category = category;
    this.supplier = supplier;
    this.colour = colour;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getWebLink() {
    return webLink;
  }

  public void setWebLink(String webLink) {
    this.webLink = webLink;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getColour() {
    return colour;
  }

  public void setColour(String colour) {
    this.colour = colour;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
