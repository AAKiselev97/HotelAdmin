package com.company.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPrintDto {
    private Integer id;
    private String name;
}
