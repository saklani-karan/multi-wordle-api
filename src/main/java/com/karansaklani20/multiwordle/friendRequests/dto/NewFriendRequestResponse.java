package com.karansaklani20.multiwordle.friendRequests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class NewFriendRequestResponse {
    private Long id;
    private String senderEmail;
    private String recipientEmail;
}
