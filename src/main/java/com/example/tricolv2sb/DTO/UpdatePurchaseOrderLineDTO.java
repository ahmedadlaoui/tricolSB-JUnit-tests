package com.example.tricolv2sb.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePurchaseOrderLineDTO {

    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private Double quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private Double unitPrice;

    @Positive(message = "Product ID must be positive")
    private Long productId;
}
