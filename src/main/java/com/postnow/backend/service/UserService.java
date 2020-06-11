package com.postnow.backend.service;

import com.postnow.backend.model.RoleEnum;
import com.postnow.backend.model.User;
import com.postnow.backend.model.UserRole;
import com.postnow.backend.repository.UserRepository;
import com.postnow.backend.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
public class UserService {
    private static final String DEFAULT_ROLE = "USER";

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

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean deleteUserByEmail(String email) {
        AtomicBoolean deleted = new AtomicBoolean(true);
        userRepository.findByEmail(email).ifPresentOrElse(user -> userRepository.delete(user), () -> {
            deleted.set(false);
        });

        return deleted.get();
    }

    public String findRole(Set<UserRole> userRoleSet){
        StringBuilder str = new StringBuilder();

        for(var urSet : userRoleSet){
            userRoleRepository.findById(urSet.getId()).ifPresent(userRole -> {
                if(userRole.getRole().equals(RoleEnum.ADMIN.name())) {
                    if (str.length() > 0) {
                        str.append(", ").append(RoleEnum.ADMIN);
                    } else {
                        str.append(RoleEnum.ADMIN);
                    }
                }
                else if(userRole.getRole().equals(RoleEnum.USER.name())) {
                    if (str.length() > 0) {
                        str.append(", ").append(RoleEnum.USER);
                    } else {
                        str.append(RoleEnum.USER);
                    }
                }
            });
        }

        return str.toString();
    }

    public Optional<UserRole> findRoleByName(RoleEnum userRoleEnum){
        return userRoleRepository.findByRole(userRoleEnum.name());
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

        userRole.ifPresent(role -> {
            user.setRoles(new HashSet<>(Collections.singletonList(role)));
        });

        userRepository.save(user);
    }

    public void updateUserByAdmin(User user){
        // validation
        if(user.getUserAdditionalData().getBirthDate().plusYears(13).isAfter(LocalDate.now()))
            throw new ValidationException("You must be at least 13 y/o");

        Optional<User> optionalUser = userRepository.findById(user.getId());
        optionalUser.ifPresent(value -> {
            value.setEmail(user.getEmail());
            value.setRoles(user.getRoles());
            value.getUserAdditionalData().setFirstName(user.getUserAdditionalData().getFirstName());
            value.getUserAdditionalData().setLastName(user.getUserAdditionalData().getLastName());
            value.getUserAdditionalData().setGender(user.getUserAdditionalData().getGender());
            value.getUserAdditionalData().setBirthDate(user.getUserAdditionalData().getBirthDate());
        });
    }

    public void updateUserBySelf(User user){
        // validation
        if((user.getPassword().length() < 8 || user.getPassword().length() > 25) && !user.getPassword().isBlank())
            throw new ValidationException("Your password must be at least 8 characters long.");
        if(user.getUserAdditionalData().getBirthDate().plusYears(13).isAfter(LocalDate.now()))
            throw new ValidationException("You must be at least 13 y/o");
        if(!(user.getUserAdditionalData().getPhoneNumber().length() == 9 || user.getUserAdditionalData().getPhoneNumber().length() == 0))
            throw new ValidationException("Your phone number is incorrect.");
        if(!user.getUserAdditionalData().getPhotoURL().matches("^(?i:http|https|ftp|ftps|sftp)://[0-9A-Za-z]+(.*)\\.(?i:jpg|jpeg|png)$")) // link to photo ends with .jpg | .jpeg | .png
            throw new ValidationException("Not allowed photo format");

        Optional<User> optionalUser = userRepository.findById(user.getId());

        optionalUser.ifPresent(value -> {
            value.setEmail(user.getEmail());
            value.getUserAdditionalData().setFirstName(user.getUserAdditionalData().getFirstName());
            value.getUserAdditionalData().setLastName(user.getUserAdditionalData().getLastName());
            value.getUserAdditionalData().setGender(user.getUserAdditionalData().getGender());
            value.getUserAdditionalData().setBirthDate(user.getUserAdditionalData().getBirthDate());
            value.getUserAdditionalData().setInRelationship(user.getUserAdditionalData().isInRelationship());
            value.getUserAdditionalData().setPhoneNumber(user.getUserAdditionalData().getPhoneNumber());
            value.getUserAdditionalData().setHomeTown(user.getUserAdditionalData().getHomeTown());
            value.getUserAdditionalData().setPhotoURL(user.getUserAdditionalData().getPhotoURL());

            if(!user.getPassword().isBlank())
                value.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        });
    }
}
