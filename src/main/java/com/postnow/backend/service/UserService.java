package com.postnow.backend.service;

import com.postnow.backend.model.User;
import com.postnow.backend.model.UserRole;
import com.postnow.backend.repository.UserRepository;
import com.postnow.backend.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_ROLE = "USER", USER_ROLE = "USER", ADMIN_ROLE = "ADMIN";

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
    public void deleteUserByEmail(String email) { userRepository.deleteUserByEmail(email); }

    public void saveUser(User user) throws ValidationException{
        // validation
        if(user.getPassword().length() < 8 || user.getPassword().length() > 25)
            throw new ValidationException("Your password must be at least 8 characters long.");
        if(user.getUserAdditionalData().getBirthDate().plusYears(13).isAfter(LocalDate.now()))
            throw new ValidationException("You must be at least 13 y/o");

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true); // todo: mail service
        Optional<UserRole> userRole = userRoleRepository.findByRole(DEFAULT_ROLE);

        // in db always is DEFAULT_ROLE - check data.sql*
        user.setRoles(new HashSet<UserRole>(Collections.singletonList(userRole.get())));
        userRepository.save(user);
    }


}
