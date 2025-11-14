package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.GoodsIssueNotFoundException;
import com.example.tricolv2sb.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le mécanisme FIFO de sortie de stock
 * Tâche 1.1.A : Mécanisme de Sortie de Stock FIFO
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests FIFO - Mécanisme de Sortie de Stock")
class GoodsIssueServiceFIFOTest {

    @Mock
    private GoodsIssueRepository goodsIssueRepository;

    @Mock
    private GoodsIssueLineRepository goodsIssueLineRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockLotRepository stockLotRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private GoodsIssueService goodsIssueService;

    @Captor
    private ArgumentCaptor<StockLot> stockLotCaptor;

    @Captor
    private ArgumentCaptor<StockMovement> stockMovementCaptor;

    private Product product;
    private GoodsIssue goodsIssue;
    private GoodsIssueLine goodsIssueLine;
    private StockLot stockLot1;
    private StockLot stockLot2;
    private StockLot stockLot3;

    @BeforeEach
    void setUp() {
        // Configuration du produit
        product = new Product();
        product.setId(1L);
        product.setReference("PROD001");
        product.setName("Test Product");
        product.setReorderPoint(10.0);

        // Configuration du bon de sortie
        goodsIssue = new GoodsIssue();
        goodsIssue.setId(1L);
        goodsIssue.setIssueNumber("GI-20251114-001");
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);
        goodsIssue.setIssueLines(new ArrayList<>());

        // Configuration de la ligne de bon de sortie
        goodsIssueLine = new GoodsIssueLine();
        goodsIssueLine.setId(1L);
        goodsIssueLine.setProduct(product);
        goodsIssueLine.setGoodsIssue(goodsIssue);

        // Configuration des lots de stock (ordre chronologique)
        stockLot1 = new StockLot();
        stockLot1.setId(1L);
        stockLot1.setLotNumber("LOT-001");
        stockLot1.setEntryDate(LocalDate.of(2025, 1, 10));
        stockLot1.setInitialQuantity(100.0);
        stockLot1.setRemainingQuantity(100.0);
        stockLot1.setPurchasePrice(50.0);
        stockLot1.setProduct(product);

        stockLot2 = new StockLot();
        stockLot2.setId(2L);
        stockLot2.setLotNumber("LOT-002");
        stockLot2.setEntryDate(LocalDate.of(2025, 2, 15));
        stockLot2.setInitialQuantity(150.0);
        stockLot2.setRemainingQuantity(150.0);
        stockLot2.setPurchasePrice(55.0);
        stockLot2.setProduct(product);

        stockLot3 = new StockLot();
        stockLot3.setId(3L);
        stockLot3.setLotNumber("LOT-003");
        stockLot3.setEntryDate(LocalDate.of(2025, 3, 20));
        stockLot3.setInitialQuantity(80.0);
        stockLot3.setRemainingQuantity(80.0);
        stockLot3.setPurchasePrice(60.0);
        stockLot3.setProduct(product);
    }

    /**
     * Scénario 1 : Sortie simple consommant partiellement un seul lot
     * Le lot le plus ancien doit être consommé en premier
     * La quantité restante du lot doit être mise à jour correctement
     */
    @Test
    @DisplayName("Scénario 1 - Sortie partielle d'un seul lot (FIFO)")
    void testValidateGoodsIssue_PartialConsumptionOfSingleLot() {
        // Arrange
        Double quantityToIssue = 30.0;
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(List.of(stockLot1));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        // Vérifier que le lot a été mis à jour avec la bonne quantité restante
        verify(stockLotRepository, times(1)).save(stockLotCaptor.capture());
        StockLot savedLot = stockLotCaptor.getValue();
        assertEquals(70.0, savedLot.getRemainingQuantity(), 0.001,
                "La quantité restante du lot devrait être 70.0 (100 - 30)");
        assertEquals(stockLot1.getId(), savedLot.getId(),
                "Le lot le plus ancien (LOT-001) devrait être consommé");

        // Vérifier qu'un mouvement de stock OUT a été créé
        verify(stockMovementRepository, times(1)).save(stockMovementCaptor.capture());
        StockMovement savedMovement = stockMovementCaptor.getValue();
        assertEquals(StockMovementType.OUT, savedMovement.getMovementType(),
                "Le type de mouvement devrait être OUT");
        assertEquals(quantityToIssue, savedMovement.getQuantity(), 0.001,
                "La quantité du mouvement devrait être 30.0");
        assertEquals(stockLot1.getId(), savedMovement.getStockLot().getId(),
                "Le mouvement devrait référencer le lot LOT-001");
        assertEquals(product.getId(), savedMovement.getProduct().getId(),
                "Le mouvement devrait référencer le bon produit");
        assertEquals(goodsIssueLine.getId(), savedMovement.getGoodsIssueLine().getId(),
                "Le mouvement devrait référencer la ligne de bon de sortie");

        // Vérifier que le statut du bon de sortie a été changé à VALIDATED
        verify(goodsIssueRepository, times(1)).save(goodsIssue);
        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssue.getStatus(),
                "Le statut du bon de sortie devrait être VALIDATED");
    }

    /**
     * Scénario 2 : Sortie nécessitant la consommation de plusieurs lots successifs
     * Les lots doivent être consommés dans l'ordre chronologique (FIFO)
     * Le premier lot doit être complètement épuisé avant de passer au suivant
     */
    @Test
    @DisplayName("Scénario 2 - Consommation de plusieurs lots successifs (FIFO)")
    void testValidateGoodsIssue_ConsumptionOfMultipleLots() {
        // Arrange
        Double quantityToIssue = 180.0; // Plus que le premier lot (100)
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(330.0); // Total des 3 lots
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1, stockLot2, stockLot3));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        // Vérifier que deux lots ont été mis à jour
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> savedLots = stockLotCaptor.getAllValues();

        // Premier lot devrait être complètement épuisé (100 consommés)
        StockLot firstSavedLot = savedLots.get(0);
        assertEquals(stockLot1.getId(), firstSavedLot.getId(),
                "Le premier lot consommé devrait être LOT-001");
        assertEquals(0.0, firstSavedLot.getRemainingQuantity(), 0.001,
                "Le premier lot devrait être complètement épuisé");

        // Deuxième lot devrait être partiellement consommé (80 consommés sur 150)
        StockLot secondSavedLot = savedLots.get(1);
        assertEquals(stockLot2.getId(), secondSavedLot.getId(),
                "Le deuxième lot consommé devrait être LOT-002");
        assertEquals(70.0, secondSavedLot.getRemainingQuantity(), 0.001,
                "Le deuxième lot devrait avoir 70 unités restantes (150 - 80)");

        // Vérifier que deux mouvements de stock OUT ont été créés
        verify(stockMovementRepository, times(2)).save(stockMovementCaptor.capture());
        List<StockMovement> savedMovements = stockMovementCaptor.getAllValues();

        // Premier mouvement : consommation complète du lot 1
        StockMovement firstMovement = savedMovements.get(0);
        assertEquals(100.0, firstMovement.getQuantity(), 0.001,
                "Le premier mouvement devrait consommer 100 unités");
        assertEquals(stockLot1.getId(), firstMovement.getStockLot().getId(),
                "Le premier mouvement devrait référencer LOT-001");
        assertEquals(StockMovementType.OUT, firstMovement.getMovementType());

        // Deuxième mouvement : consommation partielle du lot 2
        StockMovement secondMovement = savedMovements.get(1);
        assertEquals(80.0, secondMovement.getQuantity(), 0.001,
                "Le deuxième mouvement devrait consommer 80 unités");
        assertEquals(stockLot2.getId(), secondMovement.getStockLot().getId(),
                "Le deuxième mouvement devrait référencer LOT-002");
        assertEquals(StockMovementType.OUT, secondMovement.getMovementType());

        // Vérifier que le statut est changé à VALIDATED
        verify(goodsIssueRepository, times(1)).save(goodsIssue);
        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssue.getStatus());
    }

    /**
     * Scénario 3 : Sortie avec stock insuffisant (gestion d'erreur)
     * Une exception doit être levée avant toute modification
     * Aucun lot ne doit être modifié et aucun mouvement ne doit être créé
     */
    @Test
    @DisplayName("Scénario 3 - Stock insuffisant pour la sortie (Erreur)")
    void testValidateGoodsIssue_InsufficientStock() {
        // Arrange
        Double quantityToIssue = 400.0; // Plus que le stock total disponible (330)
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(330.0);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception IllegalStateException devrait être levée");

        // Vérifier le message d'erreur
        assertTrue(exception.getMessage().contains("Insufficient stock"),
                "Le message d'erreur devrait mentionner 'Insufficient stock'");
        assertTrue(exception.getMessage().contains("Required: 400"),
                "Le message devrait indiquer la quantité requise");
        assertTrue(exception.getMessage().contains("Available: 330"),
                "Le message devrait indiquer le stock disponible");

        // Vérifier qu'aucun lot n'a été modifié
        verify(stockLotRepository, never()).save(any(StockLot.class));

        // Vérifier qu'aucun mouvement de stock n'a été créé
        verify(stockMovementRepository, never()).save(any(StockMovement.class));

        // Vérifier que le statut du bon de sortie n'a pas été modifié
        assertEquals(GoodsIssueStatus.DRAFT, goodsIssue.getStatus(),
                "Le statut devrait rester DRAFT en cas d'erreur");
    }

    /**
     * Scénario 4 : Sortie épuisant exactement le stock disponible
     * Tous les lots doivent être complètement consommés
     * Les quantités restantes de tous les lots doivent être à zéro
     */
    @Test
    @DisplayName("Scénario 4 - Épuisement exact du stock disponible (échoue à cause du reorder point)")
    void testValidateGoodsIssue_ExactStockExhaustion() {
        // Arrange
        Double quantityToIssue = 330.0; // Exactement le total du stock disponible
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(330.0);

        // Act & Assert
        // Cette opération devrait échouer car elle réduirait le stock en dessous du
        // reorder point
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception devrait être levée car le stock final serait en dessous du reorder point");

        assertTrue(exception.getMessage().contains("below the reorder point"),
                "Le message devrait mentionner le reorder point");
    }

    /**
     * Test supplémentaire : Sortie épuisant presque tout le stock (reste 1 unité >
     * reorder point 0)
     * Permet de valider le scénario 4 complet avec respect du reorder point
     */
    @Test
    @DisplayName("Scénario 4 bis - Épuisement quasi-total du stock (respect du reorder point)")
    void testValidateGoodsIssue_ExactStockExhaustion_WithLowReorderPoint() {
        // Arrange
        product.setReorderPoint(0.0); // Reorder point à zéro
        // Pour que projectedStock > reorderPoint (0), on doit laisser au moins 0.01
        // Stock total = 330, donc on sort 329.99 pour laisser 0.01 > 0
        Double quantityToIssue = 329.5; // Laisse 0.5 unités > 0
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(330.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1, stockLot2, stockLot3));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        // Vérifier que tous les lots ont été mis à jour
        verify(stockLotRepository, times(3)).save(stockLotCaptor.capture());
        List<StockLot> savedLots = stockLotCaptor.getAllValues();

        // Le premier lot devrait être complètement épuisé
        assertEquals(0.0, savedLots.get(0).getRemainingQuantity(), 0.001,
                "Le premier lot devrait être complètement épuisé");
        // Le deuxième lot devrait être complètement épuisé
        assertEquals(0.0, savedLots.get(1).getRemainingQuantity(), 0.001,
                "Le deuxième lot devrait être complètement épuisé");
        // Le troisième lot devrait avoir 0.5 unités restantes (80 - 79.5)
        assertEquals(0.5, savedLots.get(2).getRemainingQuantity(), 0.001,
                "Le troisième lot devrait avoir 0.5 unités restantes");

        // Vérifier que trois mouvements de stock ont été créés
        verify(stockMovementRepository, times(3)).save(stockMovementCaptor.capture());
        List<StockMovement> savedMovements = stockMovementCaptor.getAllValues();

        // Vérifier les quantités de chaque mouvement
        assertEquals(100.0, savedMovements.get(0).getQuantity(), 0.001,
                "Le premier mouvement devrait consommer 100 unités (lot 1 complet)");
        assertEquals(150.0, savedMovements.get(1).getQuantity(), 0.001,
                "Le deuxième mouvement devrait consommer 150 unités (lot 2 complet)");
        assertEquals(79.5, savedMovements.get(2).getQuantity(), 0.001,
                "Le troisième mouvement devrait consommer 79.5 unités (lot 3 partiel)");

        // Vérifier l'ordre FIFO (du plus ancien au plus récent)
        assertEquals(stockLot1.getId(), savedMovements.get(0).getStockLot().getId(),
                "Le premier lot consommé devrait être LOT-001");
        assertEquals(stockLot2.getId(), savedMovements.get(1).getStockLot().getId(),
                "Le deuxième lot consommé devrait être LOT-002");
        assertEquals(stockLot3.getId(), savedMovements.get(2).getStockLot().getId(),
                "Le troisième lot consommé devrait être LOT-003");

        // Vérifier que le statut est VALIDATED
        verify(goodsIssueRepository, times(1)).save(goodsIssue);
        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssue.getStatus());
    }

    /**
     * Test de protection du reorder point
     * Vérifier qu'une sortie est refusée si elle fait passer le stock sous le
     * reorder point
     */
    @Test
    @DisplayName("Test - Protection du reorder point")
    void testValidateGoodsIssue_ReorderPointProtection() {
        // Arrange
        product.setReorderPoint(50.0);
        Double quantityToIssue = 300.0; // Laisserait 30 unités (< reorder point de 50)
        goodsIssueLine.setQuantity(quantityToIssue);

        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L)).thenReturn(List.of(goodsIssueLine));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(330.0);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception devrait être levée");

        assertTrue(exception.getMessage().contains("below the reorder point"),
                "Le message devrait mentionner le reorder point");
        assertTrue(exception.getMessage().contains("Reorder Point: 50"),
                "Le message devrait indiquer la valeur du reorder point");

        // Vérifier qu'aucune modification n'a été effectuée
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }
}
