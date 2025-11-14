package com.example.tricolv2sb.Controller.ControllerInterfaces;

import com.example.tricolv2sb.DTO.CreateProductDTO;
import com.example.tricolv2sb.DTO.ReadProductDTO;
import com.example.tricolv2sb.DTO.UpdateProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

public interface ProductControllerInterface {
    
    @GetMapping
    ResponseEntity<List<ReadProductDTO>> getAllProducts();
    
    @GetMapping("/{id}")
    ResponseEntity<ReadProductDTO> getProductById(@PathVariable Long id);
    
    @PostMapping
    ResponseEntity<ReadProductDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO);
    
    @PutMapping("/{id}")
    ResponseEntity<ReadProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDTO updateProductDTO);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id);
}