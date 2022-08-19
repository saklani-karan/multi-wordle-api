package com.rodeotech.rodeotechapi.users.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rodeotech.rodeotechapi.users.dto.AddRoleToUserRequest;
import com.rodeotech.rodeotechapi.users.dto.AddRoleToUserResponse;
import com.rodeotech.rodeotechapi.users.dto.CreateRoleRequest;
import com.rodeotech.rodeotechapi.users.dto.CreateUserRequest;
import com.rodeotech.rodeotechapi.users.dto.UserResponse;
import com.rodeotech.rodeotechapi.users.exceptions.InvalidRoleException;
import com.rodeotech.rodeotechapi.users.exceptions.RoleExistsException;
import com.rodeotech.rodeotechapi.users.exceptions.UserExistsException;
import com.rodeotech.rodeotechapi.users.exceptions.UserNotFoundException;
import com.rodeotech.rodeotechapi.users.exceptions.UserRoleExistsException;
import com.rodeotech.rodeotechapi.users.models.Role;
import com.rodeotech.rodeotechapi.users.models.User;
import com.rodeotech.rodeotechapi.users.repository.RoleRepository;
import com.rodeotech.rodeotechapi.users.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername username {}", username);
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            log.error("loadUserByUsername user not found for username {}", username);
            throw new UsernameNotFoundException(String.format("User not found for username {}", username));
        }
        log.info("loadUserByUsername username {} password {}", user.getUsername(), user.getPassword());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        });
        log.info("loadUserByUsername authorities found {}", authorities.toString());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    public List<UserResponse> getAllUsers() {
        log.info("getAllUsers called at {}", new Date().toString());
        List<User> users = this.userRepository.findAll();
        log.info("getAllUsers users found {}", users.toString());
        return users.stream().map((User user) -> {
            return this.convertToResponse(user);
        }).toList();
    }

    public User getUser(Long userId) {
        log.info("getUser userId {}", userId);
        return this.userRepository.getReferenceById(userId);
    }

    public UserResponse createUser(CreateUserRequest userRequest) throws Exception {
        log.info("createUser userRequest {}", userRequest.toString());
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        log.info("createUser encoded password {}", userRequest.getPassword());
        User prevUser = this.userRepository.findByUsername(userRequest.getUsername());
        if (prevUser != null) {
            log.info("createUser encoded prevUser {}", prevUser.toString());
            throw new UserExistsException(prevUser.getId());
        }
        log.info("createUser new user found");
        List<Role> defaultRoles = this.roleRepository.findByIsDefault(true);
        log.info("createUser default roles for the new user {}", defaultRoles.toString());
        User user = this.userRepository.save(
                User.builder()
                        .username(userRequest.getUsername())
                        .email(userRequest.getEmail())
                        .password(userRequest.getPassword())
                        .roles(defaultRoles)
                        .build());
        log.info("createUser new user {}", user.toString());
        return this.convertToResponse(user);
    }

    public UserResponse getUserById(Long id) throws Exception {
        log.info("getUserById id {}", id);
        User user = this.userRepository.getReferenceById(id);
        return convertToResponse(user);
    }

    public Role addRole(CreateRoleRequest roleRequest) throws Exception {
        log.info("addRole roleRequest", roleRequest.toString());
        if (this.roleRepository.findByRole(roleRequest.getRole()) != null) {
            log.error("addRole role already exists {}", roleRequest.getRole());
            throw new RoleExistsException(roleRequest.getRole());
        }
        log.info("addRole new role found {}", roleRequest.getRole());
        return this.roleRepository.save(
                Role
                        .builder()
                        .role(roleRequest.getRole())
                        .isDefault(roleRequest.getIsDefault())
                        .build());
    }

    public AddRoleToUserResponse addRoleToUser(Long userId, AddRoleToUserRequest addRoleRequest) throws Exception {
        log.info("addRoleToUser addRoleRequest {} received", addRoleRequest.toString());
        Role role = this.roleRepository.findByRole(addRoleRequest.getRole());
        if (role == null) {
            log.error("Invalid role name {}", addRoleRequest.getRole());
            throw new InvalidRoleException(addRoleRequest.getRole());
        }
        log.info("addRoleToUser role found with id {}", role.getId());
        Optional<User> user = this.userRepository.findById(userId);
        if (!user.isPresent()) {
            log.error("addRoleToUser user not found with id {}", userId);
            throw new UserNotFoundException(userId);
        }
        log.info("addRoleToUser updating user with username = {}", user.get().getUsername());
        List<Role> userRoles = user.get().getRoles();
        try {
            userRoles.forEach(userRole -> {
                if (userRole.getId() == role.getId()) {
                    log.error("addRoleToUser role {} already exists on user {}", role.getRole(),
                            user.get().getUsername());
                    throw new RuntimeException("ROLE_EXISTS_ON_USER");
                }
            });
        } catch (Exception exception) {
            if (exception.getMessage().equals("ROLE_EXISTS_ON_USER")) {
                throw new UserRoleExistsException(role.getRole(), user.get().getUsername());
            }
            throw exception;
        }
        log.info("addRoleToUser new role encountered for user");
        userRoles.add(role);
        user.get().setRoles(userRoles);
        this.userRepository.save(user.get());
        log.info("addRoleToUser user update with new user roles {}", userRoles);
        return AddRoleToUserResponse.builder().success(true).userId(userId).build();
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse
                .builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .id(user.getId())
                .build();
    }
}
