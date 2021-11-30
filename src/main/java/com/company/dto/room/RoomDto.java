package com.company.dto.room;

import com.company.model.RoomStatus;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
public class RoomDto {
    @NotNull
    private Integer cost;
    @NotNull
    private RoomStatus status;
    @NotNull
    private Integer roomNumber;
}
