package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Entity.GoodsIssue;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { GoodsIssueLineMapper.class })
public interface GoodsIssueMapper {

    ReadGoodsIssueDTO toDto(GoodsIssue goodsIssue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issueNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issueLines", ignore = true)
    GoodsIssue toEntity(CreateGoodsIssueDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issueNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "issueLines", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateGoodsIssueDTO dto, @MappingTarget GoodsIssue entity);
}
