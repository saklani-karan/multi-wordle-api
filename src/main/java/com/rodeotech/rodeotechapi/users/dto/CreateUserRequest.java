package com.rodeotech.rodeotechapi.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
}
