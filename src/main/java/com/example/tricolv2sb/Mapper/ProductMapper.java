package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreateProductDTO;
import com.example.tricolv2sb.DTO.ReadProductDTO;
import com.example.tricolv2sb.DTO.UpdateProductDTO;
import com.example.tricolv2sb.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrderLines", ignore = true)
    @Mapping(target = "goodsIssueLines", ignore = true)
    @Mapping(target = "stockLots", ignore = true)
    @Mapping(target = "stockMovements", ignore = true)
    Product toEntity(CreateProductDTO createDto);

    ReadProductDTO toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "purchaseOrderLines", ignore = true)
    @Mapping(target = "goodsIssueLines", ignore = true)
    @Mapping(target = "stockLots", ignore = true)
    @Mapping(target = "stockMovements", ignore = true)
    void updateEntity(UpdateProductDTO updateDto, @MappingTarget Product product);
}