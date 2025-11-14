package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdatePurchaseOrderDTO {
    
    @NotNull(message = "Status is required")
    private OrderStatus status;
    
    private Double totalAmount;
    private LocalDateTime receptionDate;
}