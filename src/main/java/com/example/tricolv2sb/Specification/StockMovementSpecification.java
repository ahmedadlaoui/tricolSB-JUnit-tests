package com.example.tricolv2sb.Specification;

import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Entity.StockMovement;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Spécifications JPA pour la recherche avancée sur les mouvements de stock
 * Permet de construire des requêtes dynamiques avec plusieurs critères
 */
public class StockMovementSpecification {

    /**
     * Filtre par période (date de début et date de fin)
     * 
     * @param dateDebut Date de début de la période (inclusive)
     * @param dateFin   Date de fin de la période (inclusive)
     */
    public static Specification<StockMovement> hasDateBetween(LocalDate dateDebut, LocalDate dateFin) {
        return (root, query, criteriaBuilder) -> {
            if (dateDebut == null && dateFin == null) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            if (dateDebut != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("movementDate"), dateDebut));
            }

            if (dateFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("movementDate"), dateFin));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filtre par ID de produit
     * 
     * @param productId ID du produit
     */
    public static Specification<StockMovement> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> {
            if (productId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("id"), productId);
        };
    }

    /**
     * Filtre par référence de produit
     * 
     * @param reference Référence du produit
     */
    public static Specification<StockMovement> hasProductReference(String reference) {
        return (root, query, criteriaBuilder) -> {
            if (reference == null || reference.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("product").get("reference"), reference);
        };
    }

    /**
     * Filtre par type de mouvement (IN ou OUT)
     * 
     * @param type Type de mouvement
     */
    public static Specification<StockMovement> hasMovementType(StockMovementType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("movementType"), type);
        };
    }

    /**
     * Filtre par numéro de lot
     * 
     * @param lotNumber Numéro du lot
     */
    public static Specification<StockMovement> hasLotNumber(String lotNumber) {
        return (root, query, criteriaBuilder) -> {
            if (lotNumber == null || lotNumber.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("stockLot").get("lotNumber"), lotNumber);
        };
    }

    /**
     * Combine tous les critères de recherche
     * 
     * @param dateDebut Date de début (optionnel)
     * @param dateFin   Date de fin (optionnel)
     * @param productId ID du produit (optionnel)
     * @param reference Référence du produit (optionnel)
     * @param type      Type de mouvement (optionnel)
     * @param lotNumber Numéro de lot (optionnel)
     */
    public static Specification<StockMovement> buildSearchSpecification(
            LocalDate dateDebut,
            LocalDate dateFin,
            Long productId,
            String reference,
            StockMovementType type,
            String lotNumber) {

        Specification<StockMovement> spec = Specification.allOf();

        if (dateDebut != null || dateFin != null) {
            spec = spec.and(hasDateBetween(dateDebut, dateFin));
        }
        if (productId != null) {
            spec = spec.and(hasProductId(productId));
        }
        if (reference != null && !reference.trim().isEmpty()) {
            spec = spec.and(hasProductReference(reference));
        }
        if (type != null) {
            spec = spec.and(hasMovementType(type));
        }
        if (lotNumber != null && !lotNumber.trim().isEmpty()) {
            spec = spec.and(hasLotNumber(lotNumber));
        }

        return spec;
    }
}
