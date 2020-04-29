package com.postnow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postnow.backend.model.UserRole;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRole(String role);
}