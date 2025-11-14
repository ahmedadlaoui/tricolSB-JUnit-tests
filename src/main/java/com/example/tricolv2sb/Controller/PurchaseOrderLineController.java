package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderLineDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderLineServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order-lines")
public class PurchaseOrderLineController {

    private final PurchaseOrderLineServiceInterface purchaseOrderLineService;

    /**
     * GET /api/v1/purchase-order-lines
     * Gets a list of all purchase order lines.
     */
    @GetMapping
    public ResponseEntity<List<ReadPurchaseOrderLineDTO>> getAllPurchaseOrderLines() {
        List<ReadPurchaseOrderLineDTO> orderLines = purchaseOrderLineService.fetchAllPurchaseOrderLines();
        return ResponseEntity.ok(orderLines);
    }

    /**
     * GET /api/v1/purchase-order-lines/{id}
     * Gets a single purchase order line by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadPurchaseOrderLineDTO> getPurchaseOrderLineById(@PathVariable Long id) {
        return purchaseOrderLineService.fetchPurchaseOrderLineById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ReadPurchaseOrderLineDTO>> getPurchaseOrderLinesByOrderId(@PathVariable Long orderId) {
        List<ReadPurchaseOrderLineDTO> orderLines = purchaseOrderLineService.fetchPurchaseOrderLinesByOrderId(orderId);
        return ResponseEntity.ok(orderLines);
    }

    /**
     * POST /api/v1/purchase-order-lines
     * Creates a new purchase order line.
     */
    @PostMapping
    public ResponseEntity<ReadPurchaseOrderLineDTO> createPurchaseOrderLine(
            @Valid @RequestBody CreatePurchaseOrderLineDTO dto) {
        ReadPurchaseOrderLineDTO newOrderLine = purchaseOrderLineService.createPurchaseOrderLine(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrderLine);
    }

    /**
     * PUT /api/v1/purchase-order-lines/{id}
     * Updates an existing purchase order line.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReadPurchaseOrderLineDTO> updatePurchaseOrderLine(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderLineDTO dto) {
        ReadPurchaseOrderLineDTO updatedOrderLine = purchaseOrderLineService.updatePurchaseOrderLine(id, dto);
        return ResponseEntity.ok(updatedOrderLine);
    }

    /**
     * DELETE /api/v1/purchase-order-lines/{id}
     * Deletes a purchase order line by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrderLine(@PathVariable Long id) {
        purchaseOrderLineService.deletePurchaseOrderLine(id);
        return ResponseEntity.noContent().build();
    }
}
