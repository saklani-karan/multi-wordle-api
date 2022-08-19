package com.rodeotech.rodeotechapi.users.controllers;

import com.rodeotech.rodeotechapi.users.dto.AddRoleToUserRequest;
import com.rodeotech.rodeotechapi.users.dto.AddRoleToUserResponse;
import com.rodeotech.rodeotechapi.users.dto.CreateRoleRequest;
import com.rodeotech.rodeotechapi.users.dto.CreateUserRequest;
import com.rodeotech.rodeotechapi.users.dto.UserResponse;
import com.rodeotech.rodeotechapi.users.models.Role;
import com.rodeotech.rodeotechapi.users.services.UserService;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/find")
    public List<UserResponse> findAllUsers() {
        return this.userService.getAllUsers();
    }

    @PostMapping("/createUser")
    public UserResponse createUser(@RequestBody CreateUserRequest userRequest)
            throws Exception {
        return this.userService.createUser(userRequest);
    }

    @PreAuthorize(value = "hasAuthority('ADMIN') and @userSecurity.userSpecificRequest(authentication, #id)")
    @GetMapping("/{id}")
    public UserResponse get(@PathVariable(value = "id") Long id)
            throws Exception {
        return this.userService.getUserById(id);
    }

    @PostMapping(value = "/addRole")
    public Role addRole(@RequestBody CreateRoleRequest roleRequest)
            throws Exception {
        return this.userService.addRole(roleRequest);
    }

    @PutMapping(value = "/{userId}/addRole")
    public AddRoleToUserResponse addRoleToUser(
            @PathVariable(value = "userId") Long userId,
            @RequestBody AddRoleToUserRequest roleToUserRequest)
            throws Exception {
        return this.userService.addRoleToUser(userId, roleToUserRequest);
    }
}
