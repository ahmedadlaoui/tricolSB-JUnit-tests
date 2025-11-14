package com.example.tricolv2sb.Controller.ControllerInterfaces;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

public interface PurchaseOrderControllerInterface {

    @GetMapping
    ResponseEntity<List<ReadPurchaseOrderDTO>> getAllPurchaseOrders();

    @GetMapping("/{id}")
    ResponseEntity<ReadPurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ReadPurchaseOrderDTO> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderDTO createPurchaseOrderDTO);

    @PutMapping("/{id}")
    ResponseEntity<ReadPurchaseOrderDTO> updatePurchaseOrder(@PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderDTO updatePurchaseOrderDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id);

    @GetMapping("/supplier/{id}")
    ResponseEntity<List<ReadPurchaseOrderDTO>> getPurchaseOrdersBySupplier(@PathVariable Long id);
}