package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByReference(String reference);
    
    List<Product> findByCategory(String category);
    
    boolean existsByReference(String reference);
}
