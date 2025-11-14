package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.PurchaseOrder;

import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplier(Supplier supplier);

    @EntityGraph(attributePaths = { "orderLines", "orderLines.product" })
    @Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id")
    Optional<PurchaseOrder> findByIdWithOrderLines(@Param("id") Long id);

    @EntityGraph(attributePaths = { "orderLines", "orderLines.product" })
    @Query("SELECT po FROM PurchaseOrder po")
    List<PurchaseOrder> findAllWithOrderLines();
}
