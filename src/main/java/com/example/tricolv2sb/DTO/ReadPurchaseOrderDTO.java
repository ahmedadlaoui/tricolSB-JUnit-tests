package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ReadPurchaseOrderDTO {

    private Long id;
    private LocalDate orderDate;
    private OrderStatus status;
    private Double totalAmount;
    private LocalDateTime receptionDate;

    private Long supplierId;
    private String supplierCompanyName;
    private List<OrderLineItemDTO> orderLines;

    @Data
    @NoArgsConstructor
    public static class OrderLineItemDTO {
        private Long id;
        private Double quantity;
        private Double unitPrice;
        private Double lineTotal;
        private Long productId;
        private String productReference;
        private String productName;
    }
}