package com.example.tricolv2sb.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadGoodsIssueLineDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double quantity;
}
