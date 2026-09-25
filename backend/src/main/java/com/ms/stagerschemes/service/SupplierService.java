package com.ms.stagerschemes.service;

import com.ms.stagerschemes.dto.SupplierRequest;
import com.ms.stagerschemes.dto.SupplierResponse;
import com.ms.stagerschemes.model.Supplier;
import com.ms.stagerschemes.repository.SupplierRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierService {

  private final SupplierRepository supplierRepository;

  public SupplierService(SupplierRepository supplierRepository) {
    this.supplierRepository = supplierRepository;
  }

  @Transactional(readOnly = true)
  public List<SupplierResponse> findAll() {
    return supplierRepository.findAll().stream().map(SupplierResponse::from).toList();
  }

  @Transactional
  public SupplierResponse create(SupplierRequest supplierRequest) {
    if (supplierRepository.existsByName(supplierRequest.name())) {
      throw new IllegalArgumentException("Supplier already exists: " + supplierRequest.name());
    }
    return SupplierResponse.from(supplierRepository.save(new Supplier(supplierRequest.name())));
  }

  @Transactional
  public void delete(Long supplierId) {
    if (!supplierRepository.existsById(supplierId)) {
      throw new NoSuchElementException("Supplier not found: " + supplierId);
    }
    supplierRepository.deleteById(supplierId);
  }
}
