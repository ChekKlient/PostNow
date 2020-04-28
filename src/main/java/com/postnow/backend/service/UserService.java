package com.postnow.backend.service;

import com.postnow.backend.model.User;
import com.postnow.backend.model.UserRole;
import com.postnow.backend.repository.UserDetailsRepository;
import com.postnow.backend.repository.UserRepository;
import com.postnow.backend.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class UserService {
    private static final String DEFAULT_ROLE = "USER", USER_ROLE = "USER", ADMIN_ROLE = "ADMIN";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        UserRole userRole = userRoleRepository.findByRole(DEFAULT_ROLE);
        user.setRoles(new HashSet<UserRole>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }


}
