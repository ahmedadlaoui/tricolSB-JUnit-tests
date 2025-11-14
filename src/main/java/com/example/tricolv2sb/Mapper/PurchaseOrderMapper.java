package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Entity.PurchaseOrder;
import com.example.tricolv2sb.Entity.PurchaseOrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    PurchaseOrder toEntity(CreatePurchaseOrderDTO createDto);

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.companyName", target = "supplierCompanyName")
    @Mapping(source = "orderLines", target = "orderLines", qualifiedByName = "mapOrderLines")
    ReadPurchaseOrderDTO toDto(PurchaseOrder purchaseOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    void updateEntity(UpdatePurchaseOrderDTO updateDto, @MappingTarget PurchaseOrder purchaseOrder);

    @Named("mapOrderLines")
    default List<ReadPurchaseOrderDTO.OrderLineItemDTO> mapOrderLines(List<PurchaseOrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty()) {
            return List.of();
        }

        return orderLines.stream()
                .map(this::mapOrderLine)
                .collect(Collectors.toList());
    }

    default ReadPurchaseOrderDTO.OrderLineItemDTO mapOrderLine(PurchaseOrderLine line) {
        ReadPurchaseOrderDTO.OrderLineItemDTO dto = new ReadPurchaseOrderDTO.OrderLineItemDTO();
        dto.setId(line.getId());
        dto.setQuantity(line.getQuantity());
        dto.setUnitPrice(line.getUnitPrice());
        dto.setLineTotal(line.getQuantity() * line.getUnitPrice());

        if (line.getProduct() != null) {
            dto.setProductId(line.getProduct().getId());
            dto.setProductReference(line.getProduct().getReference());
            dto.setProductName(line.getProduct().getName());
        }

        return dto;
    }
}