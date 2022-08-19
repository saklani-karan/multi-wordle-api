package com.rodeotech.rodeotechapi.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddRoleToUserResponse {
    private Boolean success;
    private Long userId;
}
