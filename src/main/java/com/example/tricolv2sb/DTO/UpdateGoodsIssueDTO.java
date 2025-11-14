package com.example.tricolv2sb.DTO;

import com.example.tricolv2sb.Entity.Enum.GoodsIssueMotif;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateGoodsIssueDTO {
    private String destination;
    private GoodsIssueMotif motif;
    private LocalDate issueDate;
}
