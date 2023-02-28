package com.karansaklani20.multiwordle.friendRequests.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karansaklani20.multiwordle.friendRequests.dto.NewFriendRequestResponse;
import com.karansaklani20.multiwordle.friendRequests.models.FriendRequest;
import com.karansaklani20.multiwordle.friendRequests.services.FriendRequestService;
import com.karansaklani20.multiwordle.friends.models.Friend;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/friendRequests")
@AllArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @GetMapping("/sent")
    public List<FriendRequest> getSentRequests() throws Exception {
        return this.friendRequestService.getSentRequestsForUser();
    }

    @GetMapping("/received")
    public List<FriendRequest> getReceivedRequests() throws Exception {
        return this.friendRequestService.getReceivedRequestsForUser();
    }

    @PostMapping("/{userId}")
    public NewFriendRequestResponse sendFriendRequest(@PathVariable(name = "userId") Long userId) throws Exception {
        return this.friendRequestService.sendFriendRequest(userId);
    }

    @PostMapping("/{id}/accept")
    public Friend acceptFriendRequest(@PathVariable(name = "id") Long id) throws Exception {
        return this.friendRequestService.acceptFriendRequest(id);
    }

}
