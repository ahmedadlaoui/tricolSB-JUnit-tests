package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.*;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Entity.StockLot;
import com.example.tricolv2sb.Exception.ProductNotFoundException;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.StockLotRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService implements StockServiceInterface {

    private final ProductRepository productRepository;
    private final StockLotRepository stockLotRepository;

    @Transactional(readOnly = true)
    public List<StockSummaryDTO> getGlobalStock() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    Double totalStock = stockLotRepository
                            .calculateTotalAvailableStock(product.getId());
                    Boolean belowThreshold = totalStock < product.getReorderPoint();

                    return new StockSummaryDTO(
                            product.getId(),
                            product.getReference(),
                            product.getName(),
                            totalStock,
                            product.getReorderPoint(),
                            belowThreshold);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductStockDetailDTO getProductStockDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + productId + " not found"));

        List<StockLot> lots = stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(productId);

        Double totalStock = lots.stream()
                .mapToDouble(StockLot::getRemainingQuantity)
                .sum();

        Double fifoValuation = lots.stream()
                .mapToDouble(lot -> lot.getRemainingQuantity() * lot.getPurchasePrice())
                .sum();

        List<StockLotDTO> lotDTOs = lots.stream()
                .map(lot -> new StockLotDTO(
                        lot.getId(),
                        lot.getLotNumber(),
                        lot.getEntryDate(),
                        lot.getRemainingQuantity(),
                        lot.getInitialQuantity(),
                        lot.getPurchasePrice()))
                .collect(Collectors.toList());

        return new ProductStockDetailDTO(
                product.getId(),
                product.getReference(),
                product.getName(),
                totalStock,
                product.getReorderPoint(),
                fifoValuation,
                lotDTOs);
    }

    @Transactional(readOnly = true)
    public StockValuationDTO getTotalValuation() {
        List<StockLot> allLots = stockLotRepository.findAll();

        Double totalValue = allLots.stream()
                .filter(lot -> lot.getRemainingQuantity() > 0)
                .mapToDouble(lot -> lot.getRemainingQuantity() * lot.getPurchasePrice())
                .sum();

        Integer totalLots = (int) allLots.stream()
                .filter(lot -> lot.getRemainingQuantity() > 0)
                .count();

        Integer totalProducts = (int) allLots.stream()
                .filter(lot -> lot.getRemainingQuantity() > 0)
                .map(lot -> lot.getProduct().getId())
                .distinct()
                .count();

        return new StockValuationDTO(totalValue, totalProducts, totalLots);
    }

}
