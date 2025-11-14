package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.ReadSupplierDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.SupplierServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierServiceInterface supplierService;

    /**
     * GET /api/v1/suppliers
     * Gets a list of all suppliers.
     */
    @GetMapping
    public ResponseEntity<List<ReadSupplierDTO>> getAllSuppliers() {
        List<ReadSupplierDTO> suppliers = supplierService.fetchAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * POST /api/v1/suppliers
     * Creates a new supplier.
     */
    @PostMapping
    public ResponseEntity<ReadSupplierDTO> createSupplier(@Valid @RequestBody CreateSupplierDTO dto) {
        ReadSupplierDTO newSupplier = supplierService.addSupplier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSupplier);
    }

    /**
     * GET /api/v1/suppliers/{id}
     * Gets a single supplier by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadSupplierDTO> getSupplierById(@PathVariable Long id) {
        return supplierService.fetchSupplier(id)
                .map(supplierDTO -> ResponseEntity.ok().body(supplierDTO))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * PUT /api/v1/suppliers/{id}
     * Updates an existing supplier.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReadSupplierDTO> updateSupplier(@PathVariable Long id,
            @Valid @RequestBody CreateSupplierDTO dto) {
        ReadSupplierDTO updatedSupplier = supplierService.updateSupplier(id, dto);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * DELETE /api/v1/suppliers/{id}
     * Deletes a supplier by their ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
