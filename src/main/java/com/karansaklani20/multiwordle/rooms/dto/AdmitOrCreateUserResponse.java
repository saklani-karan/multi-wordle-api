package com.karansaklani20.multiwordle.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmitOrCreateUserResponse {
    private Long roomId;
    private Long userId;
    private Long roomUserId;
    private Boolean isAdmin;
}
