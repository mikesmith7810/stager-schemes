package com.ms.stagerschemes.dto;

import com.ms.stagerschemes.model.Supplier;

public record SupplierResponse(Long id, String name) {

  public static SupplierResponse from(Supplier supplier) {
    return new SupplierResponse(supplier.getId(), supplier.getName());
  }
}
