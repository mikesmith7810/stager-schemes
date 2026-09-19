package com.ms.stagerschemes.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scheme")
public class Scheme {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
  private BigDecimal transportCost = BigDecimal.ZERO;

  @Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
  private BigDecimal stagingCost = BigDecimal.ZERO;

  @Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
  private BigDecimal designCost = BigDecimal.ZERO;

  @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
  private boolean template = false;

  @Column(columnDefinition = "TEXT")
  private String customerSummaryOverrides;

  @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SchemeRoom> schemeRooms = new ArrayList<>();

  protected Scheme() {}

  public Scheme(String name) {
    this.name = name;
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

  public BigDecimal getTransportCost() {
    return transportCost;
  }

  public void setTransportCost(BigDecimal transportCost) {
    this.transportCost = transportCost;
  }

  public BigDecimal getStagingCost() {
    return stagingCost;
  }

  public void setStagingCost(BigDecimal stagingCost) {
    this.stagingCost = stagingCost;
  }

  public BigDecimal getDesignCost() {
    return designCost;
  }

  public void setDesignCost(BigDecimal designCost) {
    this.designCost = designCost;
  }

  public boolean isTemplate() {
    return template;
  }

  public void setTemplate(boolean template) {
    this.template = template;
  }

  public String getCustomerSummaryOverrides() {
    return customerSummaryOverrides;
  }

  public void setCustomerSummaryOverrides(String customerSummaryOverrides) {
    this.customerSummaryOverrides = customerSummaryOverrides;
  }

  public List<SchemeRoom> getSchemeRooms() {
    return schemeRooms;
  }
}
