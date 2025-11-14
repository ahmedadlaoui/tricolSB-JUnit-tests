package com.example.tricolv2sb.DTO;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadSupplierDTO {

    private Long id;

    private String companyName;

    @Email
    private String email;

    private String phone;
}
