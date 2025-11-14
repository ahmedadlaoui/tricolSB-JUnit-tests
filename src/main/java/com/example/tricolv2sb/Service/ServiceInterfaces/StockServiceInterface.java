package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.ProductStockDetailDTO;
import com.example.tricolv2sb.DTO.StockSummaryDTO;
import com.example.tricolv2sb.DTO.StockValuationDTO;

import java.util.List;

/**
 * Service interface for managing Stock and inventory
 */
public interface StockServiceInterface {

    List<StockSummaryDTO> getGlobalStock();

    ProductStockDetailDTO getProductStockDetail(Long productId);

    StockValuationDTO getTotalValuation();


}
