package com.postnow.backend.repository;

import com.postnow.backend.model.UserAdditionalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserAdditionalData, Long> {
}
