package com.example.tricolv2sb.DTO;

import lombok.Data;

@Data
public class ReadProductDTO {
    private Long id;
    private String reference;
    private String name;
    private String description;
    private Integer unitPrice;
    private String category;
    private Double reorderPoint;
    private String unitOfMeasure;
}