package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.ReadSupplierDTO;
import com.example.tricolv2sb.Entity.Supplier;
import com.example.tricolv2sb.Exception.SupplierAlreadyExistsException;
import com.example.tricolv2sb.Exception.SupplierNotFoundException;
import com.example.tricolv2sb.Mapper.SupplierMapper;
import com.example.tricolv2sb.Repository.SupplierRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.SupplierServiceInterface;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierServiceInterface {

    private final SupplierMapper supplierMapper;
    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<ReadSupplierDTO> fetchAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierMapper::toDto)
                .toList();
    }

    @Transactional
    public ReadSupplierDTO addSupplier(CreateSupplierDTO dto) {
        supplierRepository.findByIce(dto.getIce())
                .ifPresent(s -> {
                    throw new SupplierAlreadyExistsException(
                            "Supplier with this ICE: " + dto.getIce() + " already exists");
                });

        Supplier supplier = supplierMapper.toEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    @Transactional(readOnly = true)
    public Optional<ReadSupplierDTO> fetchSupplier(Long id) {
        return Optional.of(
                supplierRepository.findById(id)
                        .map(supplierMapper::toDto)
                        .orElseThrow(() -> new SupplierNotFoundException("Supplier with ID " + id + " not found")));
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier with ID " + id + " not found"));

        if (!supplier.getPurchaseOrders().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete supplier with existing purchase orders. Delete all purchase orders first.");
        }

        supplierRepository.deleteById(id);
    }

    @Transactional
    public ReadSupplierDTO updateSupplier(Long id, CreateSupplierDTO dto) {

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier with ID " + id + " not found"));
        supplierMapper.updateFromDto(dto, existingSupplier);
        Supplier savedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDto(savedSupplier);
    }
}