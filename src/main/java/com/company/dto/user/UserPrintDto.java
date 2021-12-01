package com.company.dto.user;

import com.company.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserPrintDto {
    private Integer id;
    private String name;
    private Set<Role> roles;
}

