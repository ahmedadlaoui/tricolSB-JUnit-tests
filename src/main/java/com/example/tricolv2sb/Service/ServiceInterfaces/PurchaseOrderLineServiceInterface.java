package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderLineDTO;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderLineServiceInterface {
    List<ReadPurchaseOrderLineDTO> fetchAllPurchaseOrderLines();

    Optional<ReadPurchaseOrderLineDTO> fetchPurchaseOrderLineById(Long id);

    List<ReadPurchaseOrderLineDTO> fetchPurchaseOrderLinesByOrderId(Long orderId);

    ReadPurchaseOrderLineDTO createPurchaseOrderLine(CreatePurchaseOrderLineDTO dto);

    ReadPurchaseOrderLineDTO updatePurchaseOrderLine(Long id, UpdatePurchaseOrderLineDTO dto);

    void deletePurchaseOrderLine(Long id);
}
