package com.company.mapper;

import com.company.dto.user.UserPrintDto;
import com.company.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapperUtil {
    public static List<UserPrintDto> userListToUserPrintDtoList(List<User> userList) {
        return userList.stream().map(user -> UserPrintDto.builder().id(user.getId()).name(user.getName()).roles(user.getRoles()).build()).collect(Collectors.toList());
    }
}
