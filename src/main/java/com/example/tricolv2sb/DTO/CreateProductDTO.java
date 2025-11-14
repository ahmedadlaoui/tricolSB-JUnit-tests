package com.example.tricolv2sb.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateProductDTO {
    @NotBlank(message = "Reference is required")
    private String reference;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be positive")
    private Integer unitPrice;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Reorder point is required")
    @Min(value = 0, message = "Reorder point must be positive")
    private Double reorderPoint;
    
    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;
}