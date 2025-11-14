package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.UpdateGoodsIssueDTO;

import java.util.List;
import java.util.Optional;

public interface GoodsIssueServiceInterface {

    List<ReadGoodsIssueDTO> fetchAllGoodsIssues();

    Optional<ReadGoodsIssueDTO> fetchGoodsIssueById(Long id);

    ReadGoodsIssueDTO createGoodsIssue(CreateGoodsIssueDTO dto);

    ReadGoodsIssueDTO updateGoodsIssue(Long id, UpdateGoodsIssueDTO dto);

    void deleteGoodsIssue(Long id);

    void validateGoodsIssue(Long id);

    void cancelGoodsIssue(Long id);
}
