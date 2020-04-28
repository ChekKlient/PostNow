package com.postnow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postnow.backend.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByRole(String role);
}