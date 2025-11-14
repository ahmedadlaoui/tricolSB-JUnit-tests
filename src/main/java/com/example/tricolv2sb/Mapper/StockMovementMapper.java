package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.ReadStockMovementDTO;
import com.example.tricolv2sb.Entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "stockLotId", source = "stockLot.id")
    @Mapping(target = "goodsIssueLineId", source = "goodsIssueLine.id")
    @Mapping(target = "purchaseOrderLineId", source = "purchasseOrderLine.id")
    ReadStockMovementDTO toDto(StockMovement stockMovement);
}
