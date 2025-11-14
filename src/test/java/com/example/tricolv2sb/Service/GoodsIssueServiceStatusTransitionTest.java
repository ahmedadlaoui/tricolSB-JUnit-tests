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
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour les transitions de statut des bons de sortie
 * Tâche 1.2 : Tests des Transitions de Statut
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests - Transitions de Statut des Bons de Sortie")
class GoodsIssueServiceStatusTransitionTest {

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
    private ArgumentCaptor<GoodsIssue> goodsIssueCaptor;

    @Captor
    private ArgumentCaptor<StockMovement> stockMovementCaptor;

    @Captor
    private ArgumentCaptor<StockLot> stockLotCaptor;

    private GoodsIssue goodsIssue;
    private GoodsIssueLine issueLine1;
    private GoodsIssueLine issueLine2;
    private Product product1;
    private Product product2;
    private StockLot stockLot1;
    private StockLot stockLot2;
    private StockLot stockLot3;

    @BeforeEach
    void setUp() {
        // Configuration des produits
        product1 = new Product();
        product1.setId(1L);
        product1.setReference("PROD001");
        product1.setName("Product 1");
        product1.setReorderPoint(10.0);

        product2 = new Product();
        product2.setId(2L);
        product2.setReference("PROD002");
        product2.setName("Product 2");
        product2.setReorderPoint(5.0);

        // Configuration du bon de sortie en statut BROUILLON
        goodsIssue = new GoodsIssue();
        goodsIssue.setId(1L);
        goodsIssue.setIssueNumber("GI-20251114-001");
        goodsIssue.setIssueDate(LocalDate.now());
        goodsIssue.setDestination("Atelier A");
        goodsIssue.setStatus(GoodsIssueStatus.DRAFT);
        goodsIssue.setIssueLines(new ArrayList<>());

        // Configuration des lignes de bon de sortie
        issueLine1 = new GoodsIssueLine();
        issueLine1.setId(1L);
        issueLine1.setProduct(product1);
        issueLine1.setQuantity(50.0);
        issueLine1.setGoodsIssue(goodsIssue);

        issueLine2 = new GoodsIssueLine();
        issueLine2.setId(2L);
        issueLine2.setProduct(product2);
        issueLine2.setQuantity(30.0);
        issueLine2.setGoodsIssue(goodsIssue);

        goodsIssue.getIssueLines().addAll(Arrays.asList(issueLine1, issueLine2));

        // Configuration des lots de stock
        stockLot1 = new StockLot();
        stockLot1.setId(1L);
        stockLot1.setLotNumber("LOT-001");
        stockLot1.setEntryDate(LocalDate.of(2025, 1, 10));
        stockLot1.setInitialQuantity(100.0);
        stockLot1.setRemainingQuantity(100.0);
        stockLot1.setPurchasePrice(50.0);
        stockLot1.setProduct(product1);

        stockLot2 = new StockLot();
        stockLot2.setId(2L);
        stockLot2.setLotNumber("LOT-002");
        stockLot2.setEntryDate(LocalDate.of(2025, 2, 15));
        stockLot2.setInitialQuantity(150.0);
        stockLot2.setRemainingQuantity(150.0);
        stockLot2.setPurchasePrice(55.0);
        stockLot2.setProduct(product1);

        stockLot3 = new StockLot();
        stockLot3.setId(3L);
        stockLot3.setLotNumber("LOT-003");
        stockLot3.setEntryDate(LocalDate.of(2025, 1, 5));
        stockLot3.setInitialQuantity(80.0);
        stockLot3.setRemainingQuantity(80.0);
        stockLot3.setPurchasePrice(60.0);
        stockLot3.setProduct(product2);
    }

    /**
     * Test 1 : Vérifier que le statut passe de DRAFT à VALIDATED lors de la
     * validation
     */
    @Test
    @DisplayName("Test 1 - Transition DRAFT → VALIDATED lors de la validation")
    void testValidateGoodsIssue_StatusTransitionFromDraftToValidated() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1, issueLine2));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(250.0);
        when(stockLotRepository.calculateTotalAvailableStock(2L)).thenReturn(80.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1, stockLot2));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(2L))
                .thenReturn(Arrays.asList(stockLot3));

        // Vérifier le statut initial
        assertEquals(GoodsIssueStatus.DRAFT, goodsIssue.getStatus(),
                "Le statut initial devrait être DRAFT");

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        verify(goodsIssueRepository, times(1)).save(goodsIssueCaptor.capture());
        GoodsIssue savedGoodsIssue = goodsIssueCaptor.getValue();

        assertEquals(GoodsIssueStatus.VALIDATED, savedGoodsIssue.getStatus(),
                "Le statut devrait être passé à VALIDATED après validation");
        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssue.getStatus(),
                "L'entité originale devrait aussi avoir le statut VALIDATED");
    }

    /**
     * Test 2 : Vérifier que la validation crée automatiquement les mouvements de
     * stock
     */
    @Test
    @DisplayName("Test 2 - Création automatique des mouvements de stock lors de la validation")
    void testValidateGoodsIssue_CreatesStockMovementsAutomatically() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1, issueLine2));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(250.0);
        when(stockLotRepository.calculateTotalAvailableStock(2L)).thenReturn(80.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(2L))
                .thenReturn(Arrays.asList(stockLot3));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        // Vérifier que des mouvements de stock ont été créés (2 lignes = minimum 2
        // mouvements)
        verify(stockMovementRepository, atLeast(2)).save(stockMovementCaptor.capture());
        List<StockMovement> movements = stockMovementCaptor.getAllValues();

        // Vérifier que tous les mouvements sont de type OUT
        for (StockMovement movement : movements) {
            assertEquals(StockMovementType.OUT, movement.getMovementType(),
                    "Tous les mouvements devraient être de type OUT");
            assertNotNull(movement.getMovementDate(),
                    "Chaque mouvement devrait avoir une date");
            assertNotNull(movement.getQuantity(),
                    "Chaque mouvement devrait avoir une quantité");
            assertTrue(movement.getQuantity() > 0,
                    "La quantité du mouvement devrait être positive");
        }
    }

    /**
     * Test 3 : Vérifier que la validation met à jour les quantités restantes dans
     * les lots
     */
    @Test
    @DisplayName("Test 3 - Mise à jour des quantités restantes lors de la validation")
    void testValidateGoodsIssue_UpdatesRemainingQuantities() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1));

        Double initialQuantity = stockLot1.getRemainingQuantity();
        Double quantityToIssue = issueLine1.getQuantity();

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        verify(stockLotRepository, times(1)).save(stockLotCaptor.capture());
        StockLot updatedLot = stockLotCaptor.getValue();

        Double expectedRemainingQuantity = initialQuantity - quantityToIssue;
        assertEquals(expectedRemainingQuantity, updatedLot.getRemainingQuantity(), 0.01,
                "La quantité restante devrait être mise à jour (100 - 50 = 50)");
        assertEquals(initialQuantity, 100.0, 0.01,
                "La quantité initiale était bien 100");
    }

    /**
     * Test 4 : Vérifier qu'on ne peut valider qu'un bon de sortie en statut DRAFT
     */
    @Test
    @DisplayName("Test 4 - Validation autorisée uniquement pour statut DRAFT")
    void testValidateGoodsIssue_OnlyDraftCanBeValidated() {
        // Arrange - Bon de sortie déjà validé
        goodsIssue.setStatus(GoodsIssueStatus.VALIDATED);
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception devrait être levée pour un bon non-DRAFT");

        assertTrue(exception.getMessage().contains("Only DRAFT"),
                "Le message devrait indiquer que seuls les bons DRAFT peuvent être validés");
        assertTrue(exception.getMessage().contains("VALIDATED"),
                "Le message devrait mentionner le statut actuel");

        // Vérifier qu'aucun mouvement n'a été créé
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
        verify(stockLotRepository, never()).save(any(StockLot.class));
    }

    /**
     * Test 5 : Vérifier la séquence complète : validation → mouvements → quantités
     * → statut
     */
    @Test
    @DisplayName("Test 5 - Workflow complet de validation")
    void testValidateGoodsIssue_CompleteWorkflow() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1));

        // Vérifications initiales
        assertEquals(GoodsIssueStatus.DRAFT, goodsIssue.getStatus());
        assertEquals(100.0, stockLot1.getRemainingQuantity(), 0.01);

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert - Vérifier l'ordre des opérations

        // 1. Les lots de stock ont été mis à jour
        verify(stockLotRepository, times(1)).save(any(StockLot.class));

        // 2. Les mouvements de stock ont été créés
        verify(stockMovementRepository, times(1)).save(stockMovementCaptor.capture());
        StockMovement movement = stockMovementCaptor.getValue();
        assertEquals(StockMovementType.OUT, movement.getMovementType());
        assertEquals(50.0, movement.getQuantity(), 0.01);
        assertEquals(product1, movement.getProduct());
        assertEquals(issueLine1, movement.getGoodsIssueLine());
        assertNotNull(movement.getStockLot());

        // 3. Le statut du bon de sortie a été changé à VALIDATED
        verify(goodsIssueRepository, times(1)).save(goodsIssueCaptor.capture());
        GoodsIssue savedGoodsIssue = goodsIssueCaptor.getValue();
        assertEquals(GoodsIssueStatus.VALIDATED, savedGoodsIssue.getStatus());
    }

    /**
     * Test 6 : Vérifier que les mouvements sont liés aux bonnes lignes de sortie
     */
    @Test
    @DisplayName("Test 6 - Mouvements liés aux lignes de bon de sortie")
    void testValidateGoodsIssue_MovementsLinkedToIssueLines() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1, issueLine2));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(250.0);
        when(stockLotRepository.calculateTotalAvailableStock(2L)).thenReturn(80.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(2L))
                .thenReturn(Arrays.asList(stockLot3));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        verify(stockMovementRepository, atLeast(2)).save(stockMovementCaptor.capture());
        List<StockMovement> movements = stockMovementCaptor.getAllValues();

        // Vérifier que chaque mouvement est lié à une ligne de bon de sortie
        for (StockMovement movement : movements) {
            assertNotNull(movement.getGoodsIssueLine(),
                    "Chaque mouvement devrait être lié à une ligne de bon de sortie");
            assertTrue(
                    movement.getGoodsIssueLine().equals(issueLine1) ||
                            movement.getGoodsIssueLine().equals(issueLine2),
                    "Le mouvement devrait être lié à l'une des lignes du bon de sortie");
        }
    }

    /**
     * Test 7 : Vérifier que la date des mouvements correspond à la date de
     * validation
     */
    @Test
    @DisplayName("Test 7 - Date des mouvements = date de validation")
    void testValidateGoodsIssue_MovementDateIsValidationDate() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(100.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1));

        LocalDate validationDate = LocalDate.now();

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        verify(stockMovementRepository, times(1)).save(stockMovementCaptor.capture());
        StockMovement movement = stockMovementCaptor.getValue();

        assertEquals(validationDate, movement.getMovementDate(),
                "La date du mouvement devrait être la date de validation (aujourd'hui)");
    }

    /**
     * Test 8 : Vérifier qu'un bon de sortie sans lignes ne peut pas être validé
     */
    @Test
    @DisplayName("Test 8 - Validation impossible sans lignes de sortie")
    void testValidateGoodsIssue_CannotValidateWithoutLines() {
        // Arrange
        goodsIssue.setIssueLines(new ArrayList<>()); // Vider les lignes
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(new ArrayList<>()); // Aucune ligne

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception devrait être levée pour un bon sans lignes");

        assertTrue(exception.getMessage().contains("without issue lines"),
                "Le message devrait mentionner l'absence de lignes");

        // Vérifier qu'aucun mouvement n'a été créé
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
        verify(stockLotRepository, never()).save(any(StockLot.class));

        // Vérifier que le statut n'a pas été modifié
        assertEquals(GoodsIssueStatus.DRAFT, goodsIssue.getStatus());
    }

    /**
     * Test 9 : Vérifier le rollback en cas d'erreur (stock insuffisant)
     */
    @Test
    @DisplayName("Test 9 - Rollback si erreur pendant la validation")
    void testValidateGoodsIssue_RollbackOnError() {
        // Arrange - Stock insuffisant pour provoquer une erreur
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(20.0); // Insuffisant

        GoodsIssueStatus initialStatus = goodsIssue.getStatus();

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> goodsIssueService.validateGoodsIssue(1L),
                "Une exception devrait être levée pour stock insuffisant");

        // Vérifier qu'aucune modification n'a été persistée
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));

        // Vérifier que le statut n'a pas changé
        assertEquals(initialStatus, goodsIssue.getStatus(),
                "Le statut devrait rester inchangé en cas d'erreur");
    }

    /**
     * Test 10 : Vérifier la validation d'un bon avec plusieurs lignes et plusieurs
     * lots
     */
    @Test
    @DisplayName("Test 10 - Validation complexe avec plusieurs lignes et lots")
    void testValidateGoodsIssue_ComplexScenarioMultipleLinesAndLots() {
        // Arrange
        when(goodsIssueRepository.findById(1L)).thenReturn(Optional.of(goodsIssue));
        when(goodsIssueLineRepository.findByGoodsIssueId(1L))
                .thenReturn(Arrays.asList(issueLine1, issueLine2));
        when(stockLotRepository.calculateTotalAvailableStock(1L)).thenReturn(250.0);
        when(stockLotRepository.calculateTotalAvailableStock(2L)).thenReturn(80.0);
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(stockLot1, stockLot2));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(2L))
                .thenReturn(Arrays.asList(stockLot3));

        // Act
        goodsIssueService.validateGoodsIssue(1L);

        // Assert
        // Vérifier que les lots ont été mis à jour
        verify(stockLotRepository, atLeast(2)).save(any(StockLot.class));

        // Vérifier que les mouvements ont été créés pour chaque ligne
        verify(stockMovementRepository, atLeast(2)).save(any(StockMovement.class));

        // Vérifier que le statut a été changé
        verify(goodsIssueRepository, times(1)).save(goodsIssueCaptor.capture());
        assertEquals(GoodsIssueStatus.VALIDATED, goodsIssueCaptor.getValue().getStatus());
    }

    /**
     * Test 11 : Vérifier que la validation d'un bon inexistant lève une exception
     */
    @Test
    @DisplayName("Test 11 - Exception pour bon de sortie inexistant")
    void testValidateGoodsIssue_NotFound() {
        // Arrange
        when(goodsIssueRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        GoodsIssueNotFoundException exception = assertThrows(GoodsIssueNotFoundException.class,
                () -> goodsIssueService.validateGoodsIssue(999L),
                "Une exception GoodsIssueNotFoundException devrait être levée");

        assertTrue(exception.getMessage().contains("999"),
                "Le message devrait contenir l'ID du bon de sortie");

        // Vérifier qu'aucune opération n'a été effectuée
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }
}
