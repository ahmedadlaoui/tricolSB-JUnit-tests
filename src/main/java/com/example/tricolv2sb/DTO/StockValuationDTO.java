package com.example.tricolv2sb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockValuationDTO {
    private Double totalValue;
    private Integer totalProducts;
    private Integer totalLots;
}
