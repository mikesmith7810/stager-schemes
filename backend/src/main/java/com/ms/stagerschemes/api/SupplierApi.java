package com.ms.stagerschemes.api;

import com.ms.stagerschemes.dto.SupplierRequest;
import com.ms.stagerschemes.dto.SupplierResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/suppliers")
public interface SupplierApi {

  @GetMapping
  ResponseEntity<List<SupplierResponse>> findAllSuppliers();

  @PostMapping
  ResponseEntity<SupplierResponse> createSupplier(@RequestBody SupplierRequest supplierRequest);

  @DeleteMapping("/{supplierId}")
  ResponseEntity<Void> deleteSupplier(@PathVariable Long supplierId);
}
