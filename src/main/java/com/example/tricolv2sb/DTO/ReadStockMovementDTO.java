package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.StockMovementType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReadStockMovementDTO {
    private Long id;
    private LocalDate movementDate;
    private Double quantity;
    private StockMovementType movementType;
    private Long productId;
    private String productName;
    private Long stockLotId;
    private Long goodsIssueLineId;
    private Long purchaseOrderLineId;
}
