package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreateGoodsIssueLineDTO;
import com.example.tricolv2sb.DTO.ReadGoodsIssueLineDTO;
import com.example.tricolv2sb.Entity.GoodsIssueLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsIssueLineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goodsIssue", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stockMovements", ignore = true)
    GoodsIssueLine toEntity(CreateGoodsIssueLineDTO dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ReadGoodsIssueLineDTO toDto(GoodsIssueLine entity);
}
