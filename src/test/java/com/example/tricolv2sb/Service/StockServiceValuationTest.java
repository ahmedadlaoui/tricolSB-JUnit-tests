package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.StockValuationDTO;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Entity.StockLot;
import com.example.tricolv2sb.Exception.ProductNotFoundException;
import com.example.tricolv2sb.Repository.ProductRepository;
import com.example.tricolv2sb.Repository.StockLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le calcul de valorisation du stock
 * Tâche 1.1.C : Calcul de Valorisation du Stock selon la méthode FIFO
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests - Calcul de Valorisation du Stock FIFO")
class StockServiceValuationTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockLotRepository stockLotRepository;

    @InjectMocks
    private StockService stockService;

    private Product product1;
    private Product product2;
    private StockLot lot1;
    private StockLot lot2;
    private StockLot lot3;
    private StockLot lot4;
    private StockLot lot5;

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

        // Configuration des lots de stock pour le produit 1 (prix différents)
        // Lot 1 : Prix ancien bas (50€)
        lot1 = new StockLot();
        lot1.setId(1L);
        lot1.setLotNumber("LOT-001");
        lot1.setEntryDate(LocalDate.of(2025, 1, 10));
        lot1.setInitialQuantity(100.0);
        lot1.setRemainingQuantity(30.0); // Partiellement consommé
        lot1.setPurchasePrice(50.0);
        lot1.setProduct(product1);

        // Lot 2 : Prix moyen (55€)
        lot2 = new StockLot();
        lot2.setId(2L);
        lot2.setLotNumber("LOT-002");
        lot2.setEntryDate(LocalDate.of(2025, 2, 15));
        lot2.setInitialQuantity(150.0);
        lot2.setRemainingQuantity(150.0); // Intact
        lot2.setPurchasePrice(55.0);
        lot2.setProduct(product1);

        // Lot 3 : Prix récent élevé (60€)
        lot3 = new StockLot();
        lot3.setId(3L);
        lot3.setLotNumber("LOT-003");
        lot3.setEntryDate(LocalDate.of(2025, 3, 20));
        lot3.setInitialQuantity(80.0);
        lot3.setRemainingQuantity(80.0); // Intact
        lot3.setPurchasePrice(60.0);
        lot3.setProduct(product1);

        // Configuration des lots pour le produit 2
        // Lot 4 : Prix 70€
        lot4 = new StockLot();
        lot4.setId(4L);
        lot4.setLotNumber("LOT-004");
        lot4.setEntryDate(LocalDate.of(2025, 1, 5));
        lot4.setInitialQuantity(50.0);
        lot4.setRemainingQuantity(25.0); // Partiellement consommé
        lot4.setPurchasePrice(70.0);
        lot4.setProduct(product2);

        // Lot 5 : Prix 75€
        lot5 = new StockLot();
        lot5.setId(5L);
        lot5.setLotNumber("LOT-005");
        lot5.setEntryDate(LocalDate.of(2025, 2, 10));
        lot5.setInitialQuantity(100.0);
        lot5.setRemainingQuantity(100.0); // Intact
        lot5.setPurchasePrice(75.0);
        lot5.setProduct(product2);
    }

    /**
     * Test 1 : Calcul de valorisation pour un produit avec plusieurs lots à prix
     * différents
     * Valorisation FIFO = somme(quantité_restante * prix_achat) pour chaque lot
     */
    @Test
    @DisplayName("Test 1 - Valorisation FIFO avec plusieurs lots à prix différents")
    void testGetProductStockDetail_FIFOValuationWithMultiplePrices() {
        // Arrange
        List<StockLot> lots = Arrays.asList(lot1, lot2, lot3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L)).thenReturn(lots);

        // Calcul attendu :
        // Lot 1: 30 * 50 = 1500
        // Lot 2: 150 * 55 = 8250
        // Lot 3: 80 * 60 = 4800
        // Total = 14550
        Double expectedValuation = 1500.0 + 8250.0 + 4800.0; // 14550

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertNotNull(result, "Le résultat ne devrait pas être null");
        assertEquals(expectedValuation, result.getFifoValuation(), 0.01,
                "La valorisation FIFO devrait être 14550€ (30*50 + 150*55 + 80*60)");
        assertEquals(260.0, result.getTotalStock(), 0.01,
                "Le stock total devrait être 260 unités (30 + 150 + 80)");

        // Vérifier que les lots sont bien listés dans l'ordre FIFO
        assertEquals(3, result.getLots().size(),
                "Il devrait y avoir 3 lots");
        assertEquals("LOT-001", result.getLots().get(0).getLotNumber(),
                "Le premier lot devrait être le plus ancien");
        assertEquals("LOT-003", result.getLots().get(2).getLotNumber(),
                "Le dernier lot devrait être le plus récent");
    }

    /**
     * Test 2 : Vérifier que la valorisation prend en compte uniquement les
     * quantités restantes
     */
    @Test
    @DisplayName("Test 2 - Valorisation basée sur quantités restantes (pas initiales)")
    void testGetProductStockDetail_ValuationUsesRemainingQuantity() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(lot1));

        // Lot 1 : Initial 100, Restant 30, Prix 50
        // Valorisation = 30 * 50 = 1500 (PAS 100 * 50 = 5000)

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertEquals(1500.0, result.getFifoValuation(), 0.01,
                "La valorisation devrait être basée sur la quantité restante (30), pas la quantité initiale (100)");
        assertEquals(30.0, result.getTotalStock(), 0.01,
                "Le stock total devrait être 30 (quantité restante)");

        // Vérifier que l'information du lot est correcte
        assertEquals(1, result.getLots().size());
        assertEquals(30.0, result.getLots().get(0).getRemainingQuantity(), 0.01,
                "La quantité restante du lot devrait être 30");
        assertEquals(100.0, result.getLots().get(0).getInitialQuantity(), 0.01,
                "La quantité initiale du lot devrait être 100");
    }

    /**
     * Test 3 : Calcul de la valorisation totale de tous les produits
     */
    @Test
    @DisplayName("Test 3 - Valorisation totale de tous les stocks")
    void testGetTotalValuation_AllProducts() {
        // Arrange
        List<StockLot> allLots = Arrays.asList(lot1, lot2, lot3, lot4, lot5);
        when(stockLotRepository.findAll()).thenReturn(allLots);

        // Calcul attendu :
        // Lot 1: 30 * 50 = 1500
        // Lot 2: 150 * 55 = 8250
        // Lot 3: 80 * 60 = 4800
        // Lot 4: 25 * 70 = 1750
        // Lot 5: 100 * 75 = 7500
        // Total = 23800
        Double expectedTotalValue = 1500.0 + 8250.0 + 4800.0 + 1750.0 + 7500.0; // 23800

        // Act
        StockValuationDTO result = stockService.getTotalValuation();

        // Assert
        assertNotNull(result, "Le résultat ne devrait pas être null");
        assertEquals(expectedTotalValue, result.getTotalValue(), 0.01,
                "La valeur totale du stock devrait être 23800€");
        assertEquals(2, result.getTotalProducts(),
                "Il devrait y avoir 2 produits distincts en stock");
        assertEquals(5, result.getTotalLots(),
                "Il devrait y avoir 5 lots avec du stock restant");
    }

    /**
     * Test 4 : Vérifier que les lots vides (quantité restante = 0) ne sont pas
     * comptés
     */
    @Test
    @DisplayName("Test 4 - Exclusion des lots vides de la valorisation")
    void testGetTotalValuation_ExcludesEmptyLots() {
        // Arrange
        StockLot emptyLot = new StockLot();
        emptyLot.setId(6L);
        emptyLot.setLotNumber("LOT-006");
        emptyLot.setEntryDate(LocalDate.of(2025, 1, 1));
        emptyLot.setInitialQuantity(100.0);
        emptyLot.setRemainingQuantity(0.0); // LOT VIDE
        emptyLot.setPurchasePrice(100.0);
        emptyLot.setProduct(product1);

        List<StockLot> allLots = Arrays.asList(lot1, emptyLot, lot2);
        when(stockLotRepository.findAll()).thenReturn(allLots);

        // Calcul attendu (sans le lot vide) :
        // Lot 1: 30 * 50 = 1500
        // Lot vide: 0 * 100 = 0 (ignoré)
        // Lot 2: 150 * 55 = 8250
        // Total = 9750
        Double expectedTotalValue = 1500.0 + 8250.0; // 9750

        // Act
        StockValuationDTO result = stockService.getTotalValuation();

        // Assert
        assertEquals(expectedTotalValue, result.getTotalValue(), 0.01,
                "La valorisation ne devrait pas inclure les lots vides");
        assertEquals(2, result.getTotalLots(),
                "Seuls 2 lots avec stock restant devraient être comptés (pas le lot vide)");
    }

    /**
     * Test 5 : Calcul de valorisation avec différents prix sur plusieurs périodes
     */
    @Test
    @DisplayName("Test 5 - Impact des variations de prix sur la valorisation")
    void testGetProductStockDetail_PriceVariationImpact() {
        // Arrange
        // Simuler 3 lots du même produit achetés à des prix croissants
        StockLot oldLot = new StockLot();
        oldLot.setId(10L);
        oldLot.setLotNumber("LOT-OLD");
        oldLot.setEntryDate(LocalDate.of(2024, 1, 1));
        oldLot.setInitialQuantity(50.0);
        oldLot.setRemainingQuantity(50.0);
        oldLot.setPurchasePrice(40.0); // Prix ancien
        oldLot.setProduct(product1);

        StockLot mediumLot = new StockLot();
        mediumLot.setId(11L);
        mediumLot.setLotNumber("LOT-MEDIUM");
        mediumLot.setEntryDate(LocalDate.of(2024, 6, 1));
        mediumLot.setInitialQuantity(50.0);
        mediumLot.setRemainingQuantity(50.0);
        mediumLot.setPurchasePrice(50.0); // Prix moyen
        mediumLot.setProduct(product1);

        StockLot newLot = new StockLot();
        newLot.setId(12L);
        newLot.setLotNumber("LOT-NEW");
        newLot.setEntryDate(LocalDate.of(2025, 1, 1));
        newLot.setInitialQuantity(50.0);
        newLot.setRemainingQuantity(50.0);
        newLot.setPurchasePrice(65.0); // Prix récent plus élevé
        newLot.setProduct(product1);

        List<StockLot> lots = Arrays.asList(oldLot, mediumLot, newLot);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L)).thenReturn(lots);

        // Calcul attendu :
        // Lot ancien: 50 * 40 = 2000
        // Lot moyen: 50 * 50 = 2500
        // Lot nouveau: 50 * 65 = 3250
        // Total = 7750
        Double expectedValuation = 2000.0 + 2500.0 + 3250.0; // 7750

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertEquals(expectedValuation, result.getFifoValuation(), 0.01,
                "La valorisation FIFO devrait refléter tous les prix d'achat différents");
        assertEquals(150.0, result.getTotalStock(), 0.01,
                "Le stock total devrait être 150 unités");

        // Vérifier que la valorisation moyenne par unité est correcte
        Double averagePricePerUnit = expectedValuation / 150.0;
        assertEquals(51.67, averagePricePerUnit, 0.01,
                "Le prix moyen pondéré devrait être ~51.67€ par unité");
    }

    /**
     * Test 6 : Valorisation d'un produit sans stock
     */
    @Test
    @DisplayName("Test 6 - Valorisation nulle pour produit sans stock")
    void testGetProductStockDetail_NoStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList()); // Aucun lot

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertEquals(0.0, result.getFifoValuation(), 0.01,
                "La valorisation devrait être 0 pour un produit sans stock");
        assertEquals(0.0, result.getTotalStock(), 0.01,
                "Le stock total devrait être 0");
        assertTrue(result.getLots().isEmpty(),
                "La liste des lots devrait être vide");
    }

    /**
     * Test 7 : Vérifier l'ordre FIFO dans le calcul (lots les plus anciens en
     * premier)
     */
    @Test
    @DisplayName("Test 7 - Ordre FIFO respecté dans le calcul de valorisation")
    void testGetProductStockDetail_FIFOOrderRespected() {
        // Arrange
        List<StockLot> lots = Arrays.asList(lot1, lot2, lot3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L)).thenReturn(lots);

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertEquals(3, result.getLots().size());

        // Vérifier l'ordre chronologique (FIFO)
        assertTrue(result.getLots().get(0).getEntryDate().isBefore(result.getLots().get(1).getEntryDate()),
                "Le premier lot devrait avoir une date d'entrée antérieure au deuxième");
        assertTrue(result.getLots().get(1).getEntryDate().isBefore(result.getLots().get(2).getEntryDate()),
                "Le deuxième lot devrait avoir une date d'entrée antérieure au troisième");

        // Vérifier que le lot le plus ancien a le prix le plus bas (dans notre cas)
        assertEquals(50.0, result.getLots().get(0).getPurchasePrice(), 0.01,
                "Le lot le plus ancien devrait avoir le prix 50€");
        assertEquals(60.0, result.getLots().get(2).getPurchasePrice(), 0.01,
                "Le lot le plus récent devrait avoir le prix 60€");
    }

    /**
     * Test 8 : Valorisation totale avec un seul produit
     */
    @Test
    @DisplayName("Test 8 - Valorisation totale avec un seul produit")
    void testGetTotalValuation_SingleProduct() {
        // Arrange
        List<StockLot> allLots = Arrays.asList(lot1, lot2);
        when(stockLotRepository.findAll()).thenReturn(allLots);

        // Calcul attendu :
        // Lot 1: 30 * 50 = 1500
        // Lot 2: 150 * 55 = 8250
        // Total = 9750
        Double expectedTotalValue = 1500.0 + 8250.0;

        // Act
        StockValuationDTO result = stockService.getTotalValuation();

        // Assert
        assertEquals(expectedTotalValue, result.getTotalValue(), 0.01);
        assertEquals(1, result.getTotalProducts(),
                "Il devrait y avoir 1 produit unique");
        assertEquals(2, result.getTotalLots(),
                "Il devrait y avoir 2 lots");
    }

    /**
     * Test 9 : Gestion d'erreur pour produit inexistant
     */
    @Test
    @DisplayName("Test 9 - Exception pour produit inexistant")
    void testGetProductStockDetail_ProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> stockService.getProductStockDetail(999L),
                "Une exception ProductNotFoundException devrait être levée");

        assertTrue(exception.getMessage().contains("999"),
                "Le message d'erreur devrait contenir l'ID du produit");
    }

    /**
     * Test 10 : Valorisation avec des nombres décimaux précis
     */
    @Test
    @DisplayName("Test 10 - Précision des calculs de valorisation")
    void testGetProductStockDetail_DecimalPrecision() {
        // Arrange
        StockLot precisionLot = new StockLot();
        precisionLot.setId(20L);
        precisionLot.setLotNumber("LOT-PRECISION");
        precisionLot.setEntryDate(LocalDate.now());
        precisionLot.setInitialQuantity(33.33);
        precisionLot.setRemainingQuantity(33.33);
        precisionLot.setPurchasePrice(45.67);
        precisionLot.setProduct(product1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(stockLotRepository.findAvailableLotsByProductIdOrderByEntryDate(1L))
                .thenReturn(Arrays.asList(precisionLot));

        // Calcul attendu : 33.33 * 45.67 = 1521.7611
        Double expectedValuation = 33.33 * 45.67;

        // Act
        ProductStockDetailDTO result = stockService.getProductStockDetail(1L);

        // Assert
        assertEquals(expectedValuation, result.getFifoValuation(), 0.01,
                "La valorisation devrait être calculée avec précision (1521.76€)");
        assertEquals(33.33, result.getTotalStock(), 0.01,
                "Le stock total devrait conserver la précision décimale");
    }
}
