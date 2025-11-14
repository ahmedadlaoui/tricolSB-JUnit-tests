package com.example.tricolv2sb.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePurchaseOrderDTO {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
}