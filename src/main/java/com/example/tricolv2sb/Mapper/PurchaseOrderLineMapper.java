package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreatePurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.ReadPurchaseOrderLineDTO;
import com.example.tricolv2sb.DTO.UpdatePurchaseOrderLineDTO;
import com.example.tricolv2sb.Entity.PurchaseOrderLine;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
    ReadPurchaseOrderLineDTO toDto(PurchaseOrderLine purchaseOrderLine);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stockLots", ignore = true)
    PurchaseOrderLine toEntity(CreatePurchaseOrderLineDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stockLots", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdatePurchaseOrderLineDTO dto, @MappingTarget PurchaseOrderLine entity);
}
