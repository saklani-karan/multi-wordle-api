package com.karansaklani20.multiwordle.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.karansaklani20.multiwordle.users.dto.FindOrCreateUserRequest;
import com.karansaklani20.multiwordle.users.dto.UserEntityResponse;
import com.karansaklani20.multiwordle.users.dto.UserMeResponse;
import com.karansaklani20.multiwordle.users.exceptions.AuthContextNotFound;
import com.karansaklani20.multiwordle.users.exceptions.UserNotFound;
import com.karansaklani20.multiwordle.users.models.User;
import com.karansaklani20.multiwordle.users.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername: request received with code {}", email);

        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            log.info("loadUserByUsername: no user found with email={}", email);
            throw new UsernameNotFoundException(String.format("No user found with email=%s", email));
        }

        log.info("loadUserByUsername: user found with id={}", user.getId());
        // for now there are no roles , implement later
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEmail(), authorities);
    }

    public User findOrCreateUser(FindOrCreateUserRequest request) {
        log.info("findOrCreateUser: request received for email {}", request.getEmail());

        User user = this.userRepository.findByEmail(request.getEmail());
        if (user == null) {
            log.info("findOrCreateUser: no user found for email={}", request.getEmail());
            return this.userRepository.save(User
                    .builder()
                    .email(request.getEmail())
                    .picture(request.getPicture())
                    .name(request.getName())
                    .givenName(request.getGivenName())
                    .build());
        }

        log.info("findOrCreateUser: user found for email={} with id={}", user.getEmail(), user.getId());
        return user;
    }

    public UserMeResponse me() throws Exception {
        log.info("me: request received");

        User user = this.getUserFromAuthContext();
        log.info("me: user found with id={}", user.getId());

        return UserMeResponse
                .builder()
                .name(user.getName())
                .givenName(user.getGivenName())
                .email(user.getEmail())
                .id(user.getId())
                .picture(user.getPicture())
                .build();
    }

    public User getUserFromAuthContext() throws Exception {
        log.info("getUserFromAuthContext: request received");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("getUserFromAuthContext: authentication fetched");

        String email = (String) authentication.getPrincipal();
        if (email == null) {
            log.error("getUserFromAuthContext: email missing from authentication context");
            throw new AuthContextNotFound();
        }
        log.info("getUserFromAuthContext: email={} fetched from authentication context", email);

        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            log.error("getUserFromAuthContext: user not found with email={}", email);
            throw new UserNotFound(email);
        }
        log.info("getUserFromAuthContext: user found with email={} and id={}", email, user.getId());
        return user;
    }

    public User getUserForId(Long id) throws Exception {
        log.info("getUserForId: request received");

        Optional<User> user = this.userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("getUserForId: user not found for id={}", id);
            throw new UserNotFound(id);
        }
        log.error("getUserForId: user foundfor id={}", id);
        return user.get();
    }

    public List<UserEntityResponse> convertToUserResponses(List<User> users) {
        return users.stream().map(this::convertToUserResponse).toList();
    }

    public List<UserEntityResponse> getUsersForRoom(Long roomId) {
        List<User> users = this.userRepository.getUsersForRoom(roomId);
        return this.convertToUserResponses(users);
    }

    public UserEntityResponse convertToUserResponse(User user) {
        return UserEntityResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .givenName(user.getGivenName())
                .email(user.getEmail())
                .picture(user.getPicture()).build();
    }

    public List<User> getAllUsers() {
        log.info("getAllUsers: received request");
        return this.userRepository.findAll();
    }
}
