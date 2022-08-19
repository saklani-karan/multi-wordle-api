package com.rodeotech.rodeotechapi.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodeotech.rodeotechapi.users.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String role);

    List<Role> findByIsDefault(Boolean isDefault);
}
