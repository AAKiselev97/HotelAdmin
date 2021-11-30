package com.company.service.impl;

import com.company.dto.user.UserDto;
import com.company.dto.user.UserPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.model.Role;
import com.company.model.User;
import com.company.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;
    private final User user1 = new User(1, "test1", "test_name1", "test_password1", Collections.singleton(new Role(1, "ROLE_USER")));
    private final User user2 = new User(1, "test2", "test_name2", "test_password2", Collections.singleton(new Role(1, "ROLE_USER")));
    private final User user3 = new User(1, "test3", "test_name3", "test_password3", Collections.singleton(new Role(1, "ROLE_ADMIN")));
    private final UserPrintDto userPrintDto1 = UserPrintDto.builder().id(user1.getId()).name(user1.getName()).build();
    private final UserPrintDto userPrintDto2 = UserPrintDto.builder().id(user2.getId()).name(user2.getName()).build();
    private final UserPrintDto userPrintDto3 = UserPrintDto.builder().id(user3.getId()).name(user3.getName()).build();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsernameIfUserNotFound() {
        when(userRepository.findByUsername("test_name1")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test_name1"));
    }

    @Test
    void saveUserWhenUserAlreadyExist() {
        when(userRepository.findByUsername("test_name1")).thenReturn(user1);
        UserDto userDto = new UserDto();
        userDto.setUsername("test_name1");
        assertThrows(CustomException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void saveUser() {
        when(userRepository.findByUsername("test_name1")).thenReturn(null);
        when(bCryptPasswordEncoder.encode("test_password1")).thenReturn("test_password1");
        UserDto userDto = new UserDto(1, "test1", "test_name1", "test_password1");
        User user = new User(null, userDto.getName(), userDto.getUsername(), userDto.getPassword(), Collections.singleton(new Role(1, "ROLE_USER")));
        when(userRepository.save(user)).thenReturn(user1);
        userService.saveUser(userDto);
    }

    @Test
    void updateWhenUserNotFound() {
        when(userRepository.findByUsername("test_name1")).thenReturn(null);
        UserDto userDto = new UserDto();
        userDto.setUsername("test_name1");
        assertThrows(UsernameNotFoundException.class, () -> userService.update(1, userDto));
    }

    @Test
    void update() {
        when(userRepository.findByUsername("test_name1")).thenReturn(user1);
        when(bCryptPasswordEncoder.encode("test_password1")).thenReturn("test_password1");
        when(userRepository.save(user1)).thenReturn(user1);
        UserDto userDto = new UserDto(1, "test1", "test_name1", "test_password1");
        userService.update(1, userDto);
    }

    @Test
    void getAllUserWithAdmin() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2, user3));
        assertThat(userService.getAllUser()).isEqualTo(Arrays.asList(userPrintDto1, userPrintDto2, userPrintDto3));
    }

    @Test
    void userToUserPrintDto() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2, user3));
        assertThat(userService.userToUserPrintDto()).isEqualTo(Arrays.asList(userPrintDto1, userPrintDto2, userPrintDto3));
    }

    @Test
    void getByValidId() {
        when(userRepository.getById(3)).thenReturn(user1);
        assertThat(userService.getById(3)).isEqualTo(user1);
    }

    @Test
    void getByInvalidId() {
        assertThrows(WrongIdException.class, () -> userService.getById(1));
    }

    @Test
    void checkInvalidUserId() {
        assertThrows(WrongIdException.class, () -> userService.getById(1));
    }
}