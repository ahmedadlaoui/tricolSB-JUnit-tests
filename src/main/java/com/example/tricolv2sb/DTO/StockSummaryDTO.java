package com.example.tricolv2sb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryDTO {
    private Long productId;
    private String productReference;
    private String productName;
    private Double totalStock;
    private Double reorderPoint;
    private Boolean belowThreshold;
}
