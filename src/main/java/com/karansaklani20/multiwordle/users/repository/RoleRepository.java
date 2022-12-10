package com.karansaklani20.multiwordle.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karansaklani20.multiwordle.users.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
