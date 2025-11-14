package com.example.tricolv2sb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDetailDTO {
    private Long productId;
    private String productReference;
    private String productName;
    private Double totalStock;
    private Double reorderPoint;
    private Double fifoValuation;
    private List<StockLotDTO> lots;
}
