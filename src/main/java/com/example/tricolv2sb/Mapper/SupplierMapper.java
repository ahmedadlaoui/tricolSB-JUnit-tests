package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreateSupplierDTO;
import com.example.tricolv2sb.DTO.ReadSupplierDTO;
import com.example.tricolv2sb.Entity.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrders", ignore = true)
    Supplier toEntity(CreateSupplierDTO createDto);

    ReadSupplierDTO toDto(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrders", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(CreateSupplierDTO dto, @MappingTarget Supplier entity);
}
