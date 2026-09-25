package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.SupplierApi;
import com.ms.stagerschemes.dto.SupplierRequest;
import com.ms.stagerschemes.dto.SupplierResponse;
import com.ms.stagerschemes.service.SupplierService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupplierController implements SupplierApi {

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @Override
  public ResponseEntity<List<SupplierResponse>> findAllSuppliers() {
    return ResponseEntity.ok(supplierService.findAll());
  }

  @Override
  public ResponseEntity<SupplierResponse> createSupplier(SupplierRequest supplierRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(supplierRequest));
  }

  @Override
  public ResponseEntity<Void> deleteSupplier(Long supplierId) {
    supplierService.delete(supplierId);
    return ResponseEntity.noContent().build();
  }
}
