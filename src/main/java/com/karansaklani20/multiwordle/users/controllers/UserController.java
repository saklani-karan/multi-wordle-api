package com.karansaklani20.multiwordle.users.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karansaklani20.multiwordle.users.dto.UserMeResponse;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.services.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/find")
    public List<User> findAllUsers() {
        return this.userService.getAllUsers();
    }

    @CrossOrigin(originPatterns = "*", allowedHeaders = "*")
    @GetMapping("/me")
    public UserMeResponse getCurrentUser() throws Exception {
        return this.userService.me();
    }
}
