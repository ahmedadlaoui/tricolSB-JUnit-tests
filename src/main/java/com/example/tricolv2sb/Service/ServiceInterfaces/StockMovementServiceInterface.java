package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;

import java.util.List;
import java.util.Optional;

public interface StockMovementServiceInterface {

    List<ReadStockMovementDTO> fetchAllStockMovements();

    Optional<ReadStockMovementDTO> fetchStockMovementById(Long id);

    List<ReadStockMovementDTO> fetchStockMovementsByProduct(Long productId);
}
