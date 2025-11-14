package com.example.tricolv2sb.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "stock_lots")
@Data
public class StockLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String lotNumber;
    
    @Column(nullable = false)
    private LocalDate entryDate;
    
    @Column(nullable = false)
    private Double purchasePrice;
    
    @Column(nullable = false)
    private Double remainingQuantity;
    
    @Column(nullable = false)
    private Double initialQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_line_id", nullable = false)
    private PurchaseOrderLine purchaseOrderLine;
    
    @OneToMany(mappedBy = "stockLot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StockMovement> stockMovements;
}