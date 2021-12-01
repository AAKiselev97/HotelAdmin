package com.company.model;

import com.company.exception.CustomException;

import java.util.Arrays;

public enum RoleEnum {
    ROLE_USER,
    ROLE_ADMIN,
    ;

    public static RoleEnum stringToRoleEnum(String command) {
        return Arrays.stream(values()).filter(roleEnum -> roleEnum.name().equalsIgnoreCase(command))
                .findFirst().orElseThrow(() -> new CustomException("Wrong parsing string to roleEnum"));
    }
}
