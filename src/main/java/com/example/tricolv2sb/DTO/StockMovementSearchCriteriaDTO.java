package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour les critères de recherche avancée sur les mouvements de stock
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementSearchCriteriaDTO {

    /**
     * Date de début de la période de recherche (inclusive)
     */
    private LocalDate dateDebut;

    /**
     * Date de fin de la période de recherche (inclusive)
     */
    private LocalDate dateFin;

    /**
     * ID du produit
     */
    private Long produitId;

    /**
     * Référence du produit
     */
    private String reference;

    /**
     * Type de mouvement (IN = entrée, OUT = sortie)
     */
    private StockMovementType type;

    /**
     * Numéro de lot
     */
    private String numeroLot;
}
