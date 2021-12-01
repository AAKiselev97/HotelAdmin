package com.company.service.impl;

import com.company.dto.user.UserDto;
import com.company.dto.user.UserPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.mapper.UserMapperUtil;
import com.company.model.Role;
import com.company.model.User;
import com.company.repository.RoleRepository;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User userFromDB = userRepository.findByUsername(userDto.getUsername());
        if (userFromDB != null) {
            log.error("User already exists");
            throw new CustomException("User already exists");
        }
        User user = new User(userDto.getName(), userDto.getUsername());
        user.setRoles(Collections.singleton(new Role(1, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User newUser = userRepository.save(user);
        log.info(String.format("Create user with ID [%d]", newUser.getId()));
    }

    @Override
    public void update(int userId, UserDto userDto) throws UsernameNotFoundException {
        if (userDto.getId().equals(userId) && getCurrentUser().getId().equals(userId)){
            log.error("Not enough rights");
            throw new CustomException("Not enough rights");
        }
        User userFromDB = userRepository.findByUsername(userDto.getUsername());
        if (userFromDB == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        userFromDB.setUsername(userDto.getUsername());
        userFromDB.setName(userFromDB.getName());
        userFromDB.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User newUser = userRepository.save(userFromDB);
        log.info(String.format("Update user with ID [%d]", newUser.getId()));
    }

    @Override
    public List<UserPrintDto> getAllUserPrintDto() {
        return UserMapperUtil.userListToUserPrintDtoList(getAll());
    }

    public User getById(int id) {
        try {
            User user = userRepository.getById(id);
            log.info("get user by ID" + user.getId());
            return user;
        } catch (Exception e) {
            throw new WrongIdException("Incorrect userId:" + id);
        }
    }

    @Override
    public UserPrintDto getCurrentUserForPrint() {
        User user = getCurrentUser();
        return UserPrintDto.builder().id(user.getId()).name(user.getName()).roles(user.getRoles()).build();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }

    //проверка на наличие Id в базе данных
    public void checkUserId(int userId) {
        getById(userId);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private List<User> getAll() {
        return userRepository.findAll();
    }
}
