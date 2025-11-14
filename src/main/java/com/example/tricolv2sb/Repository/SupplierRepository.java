package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    Optional<Supplier> findByIce(String ice);
}
