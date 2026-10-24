package com.aicte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstitutionRequest {

    @NotBlank(message = "Institution name is required")
    @Size(max = 255, message = "Institution name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Institution code is required")
    @Size(max = 50, message = "Institution code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Institution type is required")
    private String type;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pin code is required")
    private String pinCode;

    private String phone;
    private String email;
    private String website;
    private String universityAffiliation;
    private Integer establishmentYear;
}
