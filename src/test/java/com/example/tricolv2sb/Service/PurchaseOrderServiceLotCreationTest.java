package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.Entity.*;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Exception.PurchaseOrderNotFoundException;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la création automatique de lots de stock
 * Tâche 1.1.B : Création Automatique de Lot lors de la réception d'une commande
 * fournisseur
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests - Création Automatique de Lot de Stock")
class PurchaseOrderServiceLotCreationTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private StockLotRepository stockLotRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Captor
    private ArgumentCaptor<StockLot> stockLotCaptor;

    @Captor
    private ArgumentCaptor<StockMovement> stockMovementCaptor;

    @Captor
    private ArgumentCaptor<PurchaseOrder> purchaseOrderCaptor;

    private Supplier supplier;
    private Product product1;
    private Product product2;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderLine orderLine1;
    private PurchaseOrderLine orderLine2;

    @BeforeEach
    void setUp() {
        // Configuration du fournisseur
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setCompanyName("Test Supplier");

        // Configuration des produits
        product1 = new Product();
        product1.setId(1L);
        product1.setReference("PROD001");
        product1.setName("Product 1");

        product2 = new Product();
        product2.setId(2L);
        product2.setReference("PROD002");
        product2.setName("Product 2");

        // Configuration de la commande fournisseur
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(100L);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setStatus(OrderStatus.VALIDATED);
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderLines(new ArrayList<>());

        // Configuration des lignes de commande
        orderLine1 = new PurchaseOrderLine();
        orderLine1.setId(1L);
        orderLine1.setProduct(product1);
        orderLine1.setQuantity(50.0);
        orderLine1.setUnitPrice(25.5);
        orderLine1.setPurchaseOrder(purchaseOrder);

        orderLine2 = new PurchaseOrderLine();
        orderLine2.setId(2L);
        orderLine2.setProduct(product2);
        orderLine2.setQuantity(100.0);
        orderLine2.setUnitPrice(30.0);
        orderLine2.setPurchaseOrder(purchaseOrder);

        purchaseOrder.setOrderLines(Arrays.asList(orderLine1, orderLine2));
    }

    /**
     * Test 1 : Vérifier qu'une réception de commande crée automatiquement un lot de
     * stock
     */
    @Test
    @DisplayName("Test 1 - Création automatique d'un lot lors de la réception")
    void testReceiveOrder_CreatesStockLotAutomatically() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L); // Simuler l'ID généré
            }
            return lot;
        });

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        // Vérifier qu'un lot a été créé pour chaque ligne de commande (2 lots)
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> createdLots = stockLotCaptor.getAllValues();

        assertEquals(2, createdLots.size(),
                "Deux lots de stock devraient être créés (un par ligne de commande)");

        // Vérifier le premier lot
        StockLot lot1 = createdLots.get(0);
        assertNotNull(lot1, "Le premier lot ne devrait pas être null");
        assertEquals(orderLine1.getQuantity(), lot1.getInitialQuantity(),
                "La quantité initiale devrait correspondre à la quantité commandée");
        assertEquals(orderLine1.getQuantity(), lot1.getRemainingQuantity(),
                "La quantité restante devrait être égale à la quantité initiale");
        assertEquals(orderLine1.getUnitPrice(), lot1.getPurchasePrice(),
                "Le prix d'achat devrait correspondre au prix unitaire de la ligne");
        assertEquals(product1, lot1.getProduct(),
                "Le produit du lot devrait correspondre au produit de la ligne");
        assertEquals(orderLine1, lot1.getPurchaseOrderLine(),
                "Le lot devrait être lié à la ligne de commande");

        // Vérifier le deuxième lot
        StockLot lot2 = createdLots.get(1);
        assertNotNull(lot2, "Le deuxième lot ne devrait pas être null");
        assertEquals(orderLine2.getQuantity(), lot2.getInitialQuantity());
        assertEquals(orderLine2.getQuantity(), lot2.getRemainingQuantity());
        assertEquals(orderLine2.getUnitPrice(), lot2.getPurchasePrice());
        assertEquals(product2, lot2.getProduct());
        assertEquals(orderLine2, lot2.getPurchaseOrderLine());
    }

    /**
     * Test 2 : Vérifier la génération correcte du numéro de lot
     * Format attendu : LOT-{orderId}-{lineId}-{YYYYMMDD}
     */
    @Test
    @DisplayName("Test 2 - Génération du numéro de lot au bon format")
    void testReceiveOrder_GeneratesCorrectLotNumber() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L);
            }
            return lot;
        });

        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String expectedLotNumber1 = "LOT-100-1-" + expectedDate;
        String expectedLotNumber2 = "LOT-100-2-" + expectedDate;

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> createdLots = stockLotCaptor.getAllValues();

        // Vérifier le format du numéro de lot 1
        assertEquals(expectedLotNumber1, createdLots.get(0).getLotNumber(),
                "Le numéro du lot 1 devrait suivre le format LOT-{orderId}-{lineId}-{YYYYMMDD}");

        // Vérifier le format du numéro de lot 2
        assertEquals(expectedLotNumber2, createdLots.get(1).getLotNumber(),
                "Le numéro du lot 2 devrait suivre le format LOT-{orderId}-{lineId}-{YYYYMMDD}");

        // Vérifier que les numéros de lot sont uniques
        assertNotEquals(createdLots.get(0).getLotNumber(), createdLots.get(1).getLotNumber(),
                "Les numéros de lot devraient être uniques");
    }

    /**
     * Test 3 : Vérifier que la date d'entrée correspond à la date de réception
     */
    @Test
    @DisplayName("Test 3 - Date d'entrée du lot = date de réception")
    void testReceiveOrder_SetsCorrectEntryDate() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L);
            }
            return lot;
        });

        LocalDate today = LocalDate.now();

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> createdLots = stockLotCaptor.getAllValues();

        // Vérifier que les dates d'entrée correspondent à aujourd'hui
        assertEquals(today, createdLots.get(0).getEntryDate(),
                "La date d'entrée du lot 1 devrait être la date de réception (aujourd'hui)");
        assertEquals(today, createdLots.get(1).getEntryDate(),
                "La date d'entrée du lot 2 devrait être la date de réception (aujourd'hui)");
    }

    /**
     * Test 4 : Vérifier l'enregistrement du prix d'achat unitaire dans le lot
     */
    @Test
    @DisplayName("Test 4 - Enregistrement correct du prix d'achat unitaire")
    void testReceiveOrder_RecordsPurchasePriceCorrectly() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L);
            }
            return lot;
        });

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> createdLots = stockLotCaptor.getAllValues();

        // Vérifier que les prix sont correctement enregistrés
        assertEquals(25.5, createdLots.get(0).getPurchasePrice(), 0.001,
                "Le prix d'achat du lot 1 devrait être 25.5");
        assertEquals(30.0, createdLots.get(1).getPurchasePrice(), 0.001,
                "Le prix d'achat du lot 2 devrait être 30.0");

        // Vérifier que les prix proviennent bien des lignes de commande
        assertEquals(orderLine1.getUnitPrice(), createdLots.get(0).getPurchasePrice(),
                "Le prix du lot 1 devrait provenir de la ligne de commande 1");
        assertEquals(orderLine2.getUnitPrice(), createdLots.get(1).getPurchasePrice(),
                "Le prix du lot 2 devrait provenir de la ligne de commande 2");
    }

    /**
     * Test 5 : Vérifier le lien entre le lot créé et la ligne de commande
     */
    @Test
    @DisplayName("Test 5 - Lien correct entre lot et ligne de commande")
    void testReceiveOrder_LinksLotToPurchaseOrderLine() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L);
            }
            return lot;
        });

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        verify(stockLotRepository, times(2)).save(stockLotCaptor.capture());
        List<StockLot> createdLots = stockLotCaptor.getAllValues();

        // Vérifier les liens avec les lignes de commande
        assertEquals(orderLine1, createdLots.get(0).getPurchaseOrderLine(),
                "Le lot 1 devrait être lié à la ligne de commande 1");
        assertEquals(orderLine2, createdLots.get(1).getPurchaseOrderLine(),
                "Le lot 2 devrait être lié à la ligne de commande 2");

        // Vérifier les liens avec les produits
        assertEquals(product1, createdLots.get(0).getProduct(),
                "Le lot 1 devrait être lié au produit 1");
        assertEquals(product2, createdLots.get(1).getProduct(),
                "Le lot 2 devrait être lié au produit 2");
    }

    /**
     * Test 6 : Vérifier la création du mouvement de stock IN lors de la réception
     */
    @Test
    @DisplayName("Test 6 - Création automatique des mouvements de stock IN")
    void testReceiveOrder_CreatesStockMovementIN() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(lot.getProduct().getId()); // Utiliser l'ID du produit pour simuler
            }
            return lot;
        });

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        // Vérifier que deux mouvements de stock ont été créés
        verify(stockMovementRepository, times(2)).save(stockMovementCaptor.capture());
        List<StockMovement> movements = stockMovementCaptor.getAllValues();

        assertEquals(2, movements.size(),
                "Deux mouvements de stock devraient être créés");

        // Vérifier le premier mouvement
        StockMovement movement1 = movements.get(0);
        assertEquals(StockMovementType.IN, movement1.getMovementType(),
                "Le type de mouvement devrait être IN (entrée)");
        assertEquals(50.0, movement1.getQuantity(), 0.001,
                "La quantité du mouvement 1 devrait être 50.0");
        assertEquals(LocalDate.now(), movement1.getMovementDate(),
                "La date du mouvement devrait être aujourd'hui");
        assertEquals(product1, movement1.getProduct(),
                "Le mouvement 1 devrait référencer le produit 1");
        assertEquals(orderLine1, movement1.getPurchasseOrderLine(),
                "Le mouvement 1 devrait référencer la ligne de commande 1");

        // Vérifier le deuxième mouvement
        StockMovement movement2 = movements.get(1);
        assertEquals(StockMovementType.IN, movement2.getMovementType(),
                "Le type de mouvement devrait être IN (entrée)");
        assertEquals(100.0, movement2.getQuantity(), 0.001,
                "La quantité du mouvement 2 devrait être 100.0");
        assertEquals(product2, movement2.getProduct(),
                "Le mouvement 2 devrait référencer le produit 2");
    }

    /**
     * Test 7 : Vérifier que le statut de la commande passe à DELIVERED
     */
    @Test
    @DisplayName("Test 7 - Statut de la commande passe à DELIVERED")
    void testReceiveOrder_UpdatesStatusToDelivered() {
        // Arrange
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));
        when(stockLotRepository.save(any(StockLot.class))).thenAnswer(invocation -> {
            StockLot lot = invocation.getArgument(0);
            if (lot.getId() == null) {
                lot.setId(1L);
            }
            return lot;
        });

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        verify(purchaseOrderRepository, times(1)).save(purchaseOrderCaptor.capture());
        PurchaseOrder savedOrder = purchaseOrderCaptor.getValue();

        assertEquals(OrderStatus.DELIVERED, savedOrder.getStatus(),
                "Le statut de la commande devrait être DELIVERED après réception");
    }

    /**
     * Test 8 : Vérifier qu'on ne peut pas recevoir une commande déjà livrée
     */
    @Test
    @DisplayName("Test 8 - Erreur si commande déjà livrée")
    void testReceiveOrder_ThrowsExceptionIfAlreadyDelivered() {
        // Arrange
        purchaseOrder.setStatus(OrderStatus.DELIVERED);
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> purchaseOrderService.receiveOrder(100L),
                "Une exception devrait être levée pour une commande déjà livrée");

        assertTrue(exception.getMessage().contains("already been received"),
                "Le message devrait indiquer que la commande a déjà été reçue");

        // Vérifier qu'aucun lot n'a été créé
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    /**
     * Test 9 : Vérifier qu'on ne peut pas recevoir une commande annulée
     */
    @Test
    @DisplayName("Test 9 - Erreur si commande annulée")
    void testReceiveOrder_ThrowsExceptionIfCancelled() {
        // Arrange
        purchaseOrder.setStatus(OrderStatus.CANCELLED);
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> purchaseOrderService.receiveOrder(100L),
                "Une exception devrait être levée pour une commande annulée");

        assertTrue(exception.getMessage().contains("cancelled"),
                "Le message devrait mentionner que la commande est annulée");

        // Vérifier qu'aucun lot n'a été créé
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    /**
     * Test 10 : Vérifier le comportement avec une commande sans lignes
     */
    @Test
    @DisplayName("Test 10 - Pas de création de lot si commande sans lignes")
    void testReceiveOrder_NoLotsCreatedIfNoOrderLines() {
        // Arrange
        purchaseOrder.setOrderLines(new ArrayList<>()); // Commande sans lignes
        when(purchaseOrderRepository.findById(100L)).thenReturn(Optional.of(purchaseOrder));

        // Act
        purchaseOrderService.receiveOrder(100L);

        // Assert
        // Vérifier qu'aucun lot n'a été créé
        verify(stockLotRepository, never()).save(any(StockLot.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));

        // Vérifier que le statut est quand même passé à DELIVERED
        verify(purchaseOrderRepository, times(1)).save(purchaseOrderCaptor.capture());
        assertEquals(OrderStatus.DELIVERED, purchaseOrderCaptor.getValue().getStatus());
    }
}
