package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockMovementServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {

    private final StockMovementServiceInterface stockMovementService;

    /**
     * GET /api/v1/stock-movements
     * Gets a list of all stock movements
     */
    @GetMapping
    public ResponseEntity<List<ReadStockMovementDTO>> getAllStockMovements() {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchAllStockMovements();
        return ResponseEntity.ok(movements);
    }

    /**
     * GET /api/v1/stock-movements/{id}
     * Gets a single stock movement by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadStockMovementDTO> getStockMovementById(@PathVariable Long id) {
        return stockMovementService.fetchStockMovementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * GET /api/v1/stock-movements/product/{productId}
     * Gets all stock movements for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReadStockMovementDTO>> getStockMovementsByProduct(@PathVariable Long productId) {
        List<ReadStockMovementDTO> movements = stockMovementService.fetchStockMovementsByProduct(productId);
        return ResponseEntity.ok(movements);
    }
}
