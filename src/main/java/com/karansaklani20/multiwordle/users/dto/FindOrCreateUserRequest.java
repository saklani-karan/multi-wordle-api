package com.karansaklani20.multiwordle.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindOrCreateUserRequest {
    private String email;
    private String name;
    private String givenName;
    private String picture;
}
