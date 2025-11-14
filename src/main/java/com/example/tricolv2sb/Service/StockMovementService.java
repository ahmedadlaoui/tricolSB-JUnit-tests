package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;
import com.example.tricolv2sb.DTO.StockMovementSearchCriteriaDTO;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Entity.StockMovement;
import com.example.tricolv2sb.Exception.StockMovementNotFoundException;
import com.example.tricolv2sb.Mapper.StockMovementMapper;
import com.example.tricolv2sb.Repository.StockMovementRepository;
import com.example.tricolv2sb.Service.ServiceInterfaces.StockMovementServiceInterface;
import com.example.tricolv2sb.Specification.StockMovementSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    /**
     * Recherche avancée de mouvements de stock avec critères multiples et
     * pagination
     * 
     * @param dateDebut Date de début de la période (optionnel)
     * @param dateFin   Date de fin de la période (optionnel)
     * @param produitId ID du produit (optionnel)
     * @param reference Référence du produit (optionnel)
     * @param type      Type de mouvement (optionnel)
     * @param numeroLot Numéro de lot (optionnel)
     * @param pageable  Paramètres de pagination
     * @return Page de mouvements de stock correspondant aux critères
     */
    @Transactional(readOnly = true)
    public Page<ReadStockMovementDTO> searchStockMovements(
            LocalDate dateDebut,
            LocalDate dateFin,
            Long produitId,
            String reference,
            StockMovementType type,
            String numeroLot,
            Pageable pageable) {

        Specification<StockMovement> spec = StockMovementSpecification.buildSearchSpecification(
                dateDebut, dateFin, produitId, reference, type, numeroLot);

        Page<StockMovement> movements = stockMovementRepository.findAll(spec, pageable);

        return movements.map(stockMovementMapper::toDto);
    }

}
