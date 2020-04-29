package com.postnow.backend.repository;

import com.postnow.backend.model.User;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Email;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Email @Length(min = 6, max = 35) String email);
    void deleteUserByEmail(@Email @Length(min = 6, max = 35) String email);
}
