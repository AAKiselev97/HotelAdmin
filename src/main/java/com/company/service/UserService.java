package com.company.service;

import com.company.dto.user.UserDto;
import com.company.dto.user.UserPrintDto;
import com.company.model.User;

import java.util.List;

public interface UserService {

    void saveUser(UserDto userDto);

    List<UserPrintDto> getAllUser();

    void update(int userId, UserDto userDto);

    User getById(int id);

    void delete(int id);

    void checkUserId(int userId);

    User getCurrentUser();
}
