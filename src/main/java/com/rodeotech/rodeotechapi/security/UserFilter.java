package com.rodeotech.rodeotechapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.rodeotech.rodeotechapi.users.models.User;
import com.rodeotech.rodeotechapi.users.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component(value = "userSecurity")
@AllArgsConstructor
@Slf4j
public class UserFilter {
    private final UserRepository userRepository;

    public boolean userSpecificRequest(Authentication authentication, Long userId) {
        log.info("userSpeciifcRequest authenticationPrincipal={} userId={}", authentication.getPrincipal(), userId);
        User user = this.userRepository.findByUsername((String) authentication.getPrincipal());
        return user.getId() == userId;
    }
}
