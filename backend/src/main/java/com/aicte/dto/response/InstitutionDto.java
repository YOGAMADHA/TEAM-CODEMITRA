package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDto {

    private UUID id;
    private String name;
    private String code;
    private String type;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String phone;
    private String email;
    private String website;
    private String universityAffiliation;
    private Integer establishmentYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
