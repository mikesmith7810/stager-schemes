package com.ms.stagerschemes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ms.stagerschemes.dto.SupplierRequest;
import com.ms.stagerschemes.dto.SupplierResponse;
import com.ms.stagerschemes.model.Supplier;
import com.ms.stagerschemes.repository.SupplierRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

  @Mock private SupplierRepository supplierRepository;

  private SupplierService supplierService;

  @BeforeEach
  void setUp() {
    supplierService = new SupplierService(supplierRepository);
  }

  @Test
  void findAll_returnsAllSuppliersAsDtos() {
    when(supplierRepository.findAll()).thenReturn(List.of(new Supplier("IKEA")));

    List<SupplierResponse> result = supplierService.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("IKEA");
  }

  @Test
  void create_newSupplier_savesAndReturnsResponse() {
    when(supplierRepository.existsByName("Next")).thenReturn(false);
    when(supplierRepository.save(any(Supplier.class))).thenReturn(new Supplier("Next"));

    SupplierResponse result = supplierService.create(new SupplierRequest("Next"));

    assertThat(result.name()).isEqualTo("Next");
    verify(supplierRepository).save(any(Supplier.class));
  }

  @Test
  void create_duplicateName_throwsIllegalArgumentException() {
    when(supplierRepository.existsByName("IKEA")).thenReturn(true);

    assertThatThrownBy(() -> supplierService.create(new SupplierRequest("IKEA")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("IKEA");
  }

  @Test
  void delete_existingSupplier_deletesSuccessfully() {
    when(supplierRepository.existsById(1L)).thenReturn(true);

    supplierService.delete(1L);

    verify(supplierRepository).deleteById(1L);
  }

  @Test
  void delete_nonExistingSupplier_throwsNoSuchElementException() {
    when(supplierRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> supplierService.delete(99L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("99");
  }
}
