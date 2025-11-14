package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.StockLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

    @Query("SELECT sl FROM StockLot sl WHERE sl.product.id = :productId AND sl.remainingQuantity > 0 ORDER BY sl.entryDate ASC, sl.id ASC")
    List<StockLot> findAvailableLotsByProductIdOrderByEntryDate(@Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(sl.remainingQuantity), 0) FROM StockLot sl WHERE sl.product.id = :productId AND sl.remainingQuantity > 0")
    Double calculateTotalAvailableStock(@Param("productId") Long productId);
}
