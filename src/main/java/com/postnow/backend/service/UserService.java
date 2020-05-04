package com.postnow.backend.service;

import com.postnow.backend.model.Role;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserRole;
import com.postnow.backend.repository.UserRepository;
import com.postnow.backend.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
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

    public String findUserRoleById(Set<UserRole> userRoleSet){
        StringBuilder str = new StringBuilder();

        for(var urSet : userRoleSet){
            if(userRoleRepository.findById(urSet.getId()).get().getRole().equals(Role.ADMIN.name()))
                str.append(Role.ADMIN);
            if(userRoleRepository.findById(urSet.getId()).get().getRole().equals(Role.USER.name())) {
                if(str.length() > 0)
                    str.append(", ");
                str.append(Role.USER);
            }
        }

        return str.toString();
    }

    public Optional<UserRole> findUserRoleByName(Role userRole){
        return userRoleRepository.findByRole(userRole.name());
    }

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

    public void updateUser(Long id, User user){
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.ifPresent(value -> {
            value.setEmail(user.getEmail());
            value.setRoles(user.getRoles());

            value.getUserAdditionalData().setFirstName(user.getUserAdditionalData().getFirstName());
            value.getUserAdditionalData().setLastName(user.getUserAdditionalData().getLastName());

            value.getUserAdditionalData().setBirthDate(user.getUserAdditionalData().getBirthDate());
            value.getUserAdditionalData().setGender(user.getUserAdditionalData().getGender());
        });
    }
}
