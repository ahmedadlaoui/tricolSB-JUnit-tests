package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import com.example.tricolv2sb.Entity.StockLot;
import com.example.tricolv2sb.Entity.StockMovement;
import liquibase.datatype.core.DateTimeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductId(Long productId);

    @Query("SELECT sm FROM  StockMovement sm WHERE sm.movementDate BETWEEN :startDate and :endDate")
    List<StockMovement> getByTypeAndDate(StockMovementType type, LocalDate startDate, LocalDate endDate);
}
