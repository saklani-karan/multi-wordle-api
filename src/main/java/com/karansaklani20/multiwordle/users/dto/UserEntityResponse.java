package com.karansaklani20.multiwordle.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEntityResponse {
    private Long id;
    private String name;
    private String email;
    private String picture;
    private String givenName;
}
