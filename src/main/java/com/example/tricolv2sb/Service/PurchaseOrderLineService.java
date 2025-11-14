package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderLineDTO;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Entity.PurchaseOrder;
import com.example.tricolv2sb.Entity.PurchaseOrderLine;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Exception.ProductNotFoundException;
import com.example.tricolv2sb.Exception.PurchaseOrderLineNotFoundException;
import com.example.tricolv2sb.Exception.PurchaseOrderNotFoundException;
import com.example.tricolv2sb.Mapper.PurchaseOrderLineMapper;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.PurchaseOrderLineRepository;
import com.example.tricolv2sb.Repository.PurchaseOrderRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderLineServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderLineService implements PurchaseOrderLineServiceInterface {
    private final PurchaseOrderLineRepository orderLineRepository;
    private final PurchaseOrderLineMapper orderLineMapper;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public List<ReadPurchaseOrderLineDTO> fetchAllPurchaseOrderLines() {
        List<PurchaseOrderLine> orderLines = orderLineRepository.findAll();

        if (orderLines.isEmpty()) {
            return List.of();
        }

        return orderLines.stream()
                .map(orderLineMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReadPurchaseOrderLineDTO> fetchPurchaseOrderLineById(Long id) {
        return Optional.of(
                orderLineRepository.findById(id)
                        .map(orderLineMapper::toDto)
                        .orElseThrow(() -> new PurchaseOrderLineNotFoundException(
                                "Purchase order line with ID " + id + " not found")));
    }

    @Transactional(readOnly = true)
    public List<ReadPurchaseOrderLineDTO> fetchPurchaseOrderLinesByOrderId(Long orderId) {
        if (!purchaseOrderRepository.existsById(orderId)) {
            throw new PurchaseOrderNotFoundException("Purchase order with ID " + orderId + " not found");
        }

        List<PurchaseOrderLine> orderLines = orderLineRepository.findByPurchaseOrderId(orderId);
        return orderLines.stream()
                .map(orderLineMapper::toDto)
                .toList();
    }

    @Transactional
    public ReadPurchaseOrderLineDTO createPurchaseOrderLine(CreatePurchaseOrderLineDTO dto) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(dto.getPurchaseOrderId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + dto.getPurchaseOrderId() + " not found"));

        validateOrderCanBeModified(purchaseOrder);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + dto.getProductId() + " not found"));

        PurchaseOrderLine orderLine = orderLineMapper.toEntity(dto);
        orderLine.setPurchaseOrder(purchaseOrder);
        orderLine.setProduct(product);

        PurchaseOrderLine savedOrderLine = orderLineRepository.save(orderLine);

        updatePurchaseOrderTotal(purchaseOrder);

        return orderLineMapper.toDto(savedOrderLine);
    }

    @Transactional
    public ReadPurchaseOrderLineDTO updatePurchaseOrderLine(Long id, UpdatePurchaseOrderLineDTO dto) {
        PurchaseOrderLine existingOrderLine = orderLineRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderLineNotFoundException(
                        "Purchase order line with ID " + id + " not found"));

        PurchaseOrder purchaseOrder = existingOrderLine.getPurchaseOrder();

        validateOrderCanBeModified(purchaseOrder);

        orderLineMapper.updateFromDto(dto, existingOrderLine);

        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product with ID " + dto.getProductId() + " not found"));
            existingOrderLine.setProduct(product);
        }

        PurchaseOrderLine savedOrderLine = orderLineRepository.save(existingOrderLine);

        updatePurchaseOrderTotal(purchaseOrder);

        return orderLineMapper.toDto(savedOrderLine);
    }

    @Transactional
    public void deletePurchaseOrderLine(Long id) {
        PurchaseOrderLine orderLine = orderLineRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderLineNotFoundException(
                        "Purchase order line with ID " + id + " not found"));

        Long purchaseOrderId = orderLine.getPurchaseOrder().getId();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order with ID " + purchaseOrderId + " not found"));

        validateOrderCanBeModified(purchaseOrder);

        long stockLotCount = orderLineRepository.countStockLotsByOrderLineId(id);

        if (stockLotCount > 0) {
            throw new IllegalStateException(
                    "Cannot delete this purchase order line because it has " + stockLotCount +
                            " associated stock lot(s). The stock has already been received and is in inventory. " +
                            "Please handle the stock lots first before deleting this line.");
        }

        purchaseOrder.getOrderLines().remove(orderLine);
        orderLineRepository.deleteById(id);

        updatePurchaseOrderTotal(purchaseOrder);
    }

    private void validateOrderCanBeModified(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot modify a delivered purchase order");
        }

        if (purchaseOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a cancelled purchase order");
        }
    }

    private void updatePurchaseOrderTotal(PurchaseOrder purchaseOrder) {
        purchaseOrder.calculateTotalAmount();
        purchaseOrderRepository.save(purchaseOrder);
    }
}
