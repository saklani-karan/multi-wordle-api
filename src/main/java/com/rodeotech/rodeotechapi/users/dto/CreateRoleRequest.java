package com.rodeotech.rodeotechapi.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CreateRoleRequest {
    private String role;
    @Builder.Default
    private Boolean isDefault = false;
}
