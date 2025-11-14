package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.CreateProductDTO;
import com.example.tricolv2sb.DTO.ReadProductDTO;
import com.example.tricolv2sb.DTO.UpdateProductDTO;

import java.util.List;

public interface ProductInterface {
    
    List<ReadProductDTO> getAllProducts();
    
    ReadProductDTO getProductById(Long id);
    
    ReadProductDTO createProduct(CreateProductDTO createProductDTO);
    
    ReadProductDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);
    
    void deleteProduct(Long id);
}
