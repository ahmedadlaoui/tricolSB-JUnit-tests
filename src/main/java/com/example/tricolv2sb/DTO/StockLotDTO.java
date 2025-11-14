package com.example.tricolv2sb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLotDTO {
    private Long id;
    private String lotNumber;
    private LocalDate entryDate;
    private Double remainingQuantity;
    private Double initialQuantity;
    private Double purchasePrice;
}
