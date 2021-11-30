package com.company.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String username;
    @NotNull
    private String password;
}
