package com.example.tricolv2sb.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSupplierDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String companyName;

    @NotBlank
    private String address;

    @NotBlank
    @Size(min = 2, max = 50)
    private String city;

    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String ice;

    @NotBlank
    private String contactPerson;
}
