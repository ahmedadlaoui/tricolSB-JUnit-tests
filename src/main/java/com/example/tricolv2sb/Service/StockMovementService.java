package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;
import com.example.tricolv2sb.Entity.StockMovement;
import com.example.tricolv2sb.Exception.StockMovementNotFoundException;
import com.example.tricolv2sb.Mapper.StockMovementMapper;
import com.example.tricolv2sb.Repository.StockMovementRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockMovementServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockMovementService implements StockMovementServiceInterface {

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;

    @Transactional(readOnly = true)
    public List<ReadStockMovementDTO> fetchAllStockMovements() {
        List<StockMovement> movements = stockMovementRepository.findAll();
        return movements.stream()
                .map(stockMovementMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReadStockMovementDTO> fetchStockMovementById(Long id) {
        return Optional.of(
                stockMovementRepository.findById(id)
                        .map(stockMovementMapper::toDto)
                        .orElseThrow(() -> new StockMovementNotFoundException(
                                "Stock movement with ID " + id + " not found")));
    }

    @Transactional(readOnly = true)
    public List<ReadStockMovementDTO> fetchStockMovementsByProduct(Long productId) {
        List<StockMovement> movements = stockMovementRepository.findByProductId(productId);
        return movements.stream()
                .map(stockMovementMapper::toDto)
                .toList();
    }


}
