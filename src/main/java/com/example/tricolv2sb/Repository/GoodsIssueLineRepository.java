package com.example.tricolv2sb.Repository;

import com.example.tricolv2sb.Entity.GoodsIssueLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsIssueLineRepository extends JpaRepository<GoodsIssueLine, Long> {

    List<GoodsIssueLine> findByGoodsIssueId(Long goodsIssueId);
}
