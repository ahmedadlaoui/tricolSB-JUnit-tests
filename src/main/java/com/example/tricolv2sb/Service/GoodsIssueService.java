package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.GoodsIssueNotFoundException;
import com.example.tricolv2sb.Exception.ProductNotFoundException;
import com.example.tricolv2sb.Mapper.GoodsIssueMapper;
import com.example.tricolv2sb.Repository.*;
import com.example.tricolv2sb.Service.ServiceInterfaces.GoodsIssueServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsIssueService implements GoodsIssueServiceInterface {

    private final GoodsIssueRepository goodsIssueRepository;
    private final GoodsIssueLineRepository goodsIssueLineRepository;
    private final ProductRepository productRepository;
    private final StockLotRepository stockLotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final GoodsIssueMapper goodsIssueMapper;

    @Transactional(readOnly = true)
    public List<ReadGoodsIssueDTO> fetchAllGoodsIssues() {
        List<GoodsIssue> goodsIssues = goodsIssueRepository.findAll();
        return goodsIssues.stream()
                .map(goodsIssueMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReadGoodsIssueDTO> fetchGoodsIssueById(Long id) {
        return Optional.of(
                goodsIssueRepository.findById(id)
                        .map(goodsIssueMapper::toDto)
                        .orElseThrow(() -> new GoodsIssueNotFoundException(
                                "Goods issue with ID " + id + " not found")));
    }

    @Transactional
    public ReadGoodsIssueDTO createGoodsIssue(CreateGoodsIssueDTO dto) {
        GoodsIssue goodsIssue = goodsIssueMapper.toEntity(dto);

        String issueNumber = generateIssueNumber();
        goodsIssue.setIssueNumber(issueNumber);
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);

        List<GoodsIssueLine> issueLines = new ArrayList<>();
        for (var lineDto : dto.getIssueLines()) {
            Product product = productRepository.findById(lineDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            "Product with ID " + lineDto.getProductId() + " not found"));

            GoodsIssueLine line = new GoodsIssueLine();
            line.setProduct(product);
            line.setQuantity(lineDto.getQuantity());
            line.setGoodsIssue(goodsIssue);
            issueLines.add(line);
        }

        goodsIssue.setIssueLines(issueLines);
        GoodsIssue savedGoodsIssue = goodsIssueRepository.save(goodsIssue);
        return goodsIssueMapper.toDto(savedGoodsIssue);
    }

    @Transactional
    public ReadGoodsIssueDTO updateGoodsIssue(Long id, UpdateGoodsIssueDTO dto) {
        GoodsIssue existingGoodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new GoodsIssueNotFoundException(
                        "Goods issue with ID " + id + " not found"));

        if (existingGoodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot update goods issue with status " + existingGoodsIssue.getStatus());
        }

        goodsIssueMapper.updateFromDto(dto, existingGoodsIssue);
        GoodsIssue savedGoodsIssue = goodsIssueRepository.save(existingGoodsIssue);
        return goodsIssueMapper.toDto(savedGoodsIssue);
    }

    @Transactional
    public void deleteGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new GoodsIssueNotFoundException(
                        "Goods issue with ID " + id + " not found"));

        if (goodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot delete goods issue with status " + goodsIssue.getStatus() +
                            ". Only DRAFT goods issues can be deleted.");
        }

        goodsIssueRepository.deleteById(id);
    }

    @Transactional
    public void validateGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new GoodsIssueNotFoundException(
                        "Goods issue with ID " + id + " not found"));

        if (goodsIssue.getStatus() != GoodsIssueStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT goods issues can be validated. Current status: " + goodsIssue.getStatus());
        }

        List<GoodsIssueLine> issueLines = goodsIssueLineRepository.findByGoodsIssueId(id);

        if (issueLines.isEmpty()) {
            throw new IllegalStateException("Cannot validate goods issue without issue lines");
        }

        for (GoodsIssueLine line : issueLines) {
            processGoodsIssueLineFIFO(line);
        }

        goodsIssue.setStatus(GoodsIssueStatus.VALIDATED);
        goodsIssueRepository.save(goodsIssue);
    }

    /**
     * Process a goods issue line using FIFO algorithm
     * Consumes stock from oldest lots first and creates OUT stock movements
     */
    private void processGoodsIssueLineFIFO(GoodsIssueLine line) {
        Long productId = line.getProduct().getId();
        Double reorderPoint = line.getProduct().getReorderPoint();
        Double requiredQuantity = line.getQuantity();

        Double availableStock = stockLotRepository.calculateTotalAvailableStock(productId);
        if (availableStock < requiredQuantity) {
            throw new IllegalStateException(
                    String.format("Insufficient stock for product ID %d. Required: %.2f, Available: %.2f",
                            productId, requiredQuantity, availableStock));
        }

        Double projectedStockAfterIssue = availableStock - requiredQuantity;
        if (projectedStockAfterIssue <= reorderPoint) {
            throw new IllegalStateException(
                    String.format(
                            "Issuing this quantity would reduce stock below the reorder point for product ID %d. " +
                                    "Reorder Point: %.2f, Stock After Issue: %.2f, Available Stock: %.2f",
                            productId, reorderPoint, projectedStockAfterIssue, availableStock));
        }

        List<StockLot> availableLots = stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(productId);

        Double remainingToConsume = requiredQuantity;

        for (StockLot lot : availableLots) {
            if (remainingToConsume <= 0) {
                break;
            }

            Double lotAvailable = lot.getRemainingQuantity();
            Double quantityToConsume = Math.min(lotAvailable, remainingToConsume);

            lot.setRemainingQuantity(lotAvailable - quantityToConsume);
            stockLotRepository.save(lot);

            StockMovement movement = new StockMovement();
            movement.setMovementType(StockMovementType.OUT);
            movement.setQuantity(quantityToConsume);
            movement.setMovementDate(LocalDate.now());
            movement.setProduct(line.getProduct());
            movement.setStockLot(lot);
            movement.setGoodsIssueLine(line);
            stockMovementRepository.save(movement);

            remainingToConsume -= quantityToConsume;
        }

        if (remainingToConsume > 0.001) {
            throw new IllegalStateException(
                    String.format("Failed to consume required quantity for product ID %d. Remaining: %.2f",
                            productId, remainingToConsume));
        }
    }

    @Transactional
    public void cancelGoodsIssue(Long id) {
        GoodsIssue goodsIssue = goodsIssueRepository.findById(id)
                .orElseThrow(() -> new GoodsIssueNotFoundException(
                        "Goods issue with ID " + id + " not found"));

        if (goodsIssue.getStatus() == GoodsIssueStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Goods issue is already cancelled");
        }

        goodsIssue.setStatus(GoodsIssueStatus.CANCELLED);
        goodsIssueRepository.save(goodsIssue);
    }

    private String generateIssueNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = goodsIssueRepository.count() + 1;
        return String.format("GI-%s-%03d", date, count);
    }
}
