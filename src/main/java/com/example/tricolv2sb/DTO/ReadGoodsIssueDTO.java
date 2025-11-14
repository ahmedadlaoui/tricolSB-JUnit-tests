package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.GoodsIssueMotif;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class ReadGoodsIssueDTO {
    private Long id;
    private String issueNumber;
    private LocalDate issueDate;
    private String destination;
    private GoodsIssueMotif motif;
    private GoodsIssueStatus status;
    private List<ReadGoodsIssueLineDTO> issueLines;
}
