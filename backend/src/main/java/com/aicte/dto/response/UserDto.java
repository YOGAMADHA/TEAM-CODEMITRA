package com.aicte.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean active;
    private List<String> roles;
    private UUID institutionId;
    private String institutionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
