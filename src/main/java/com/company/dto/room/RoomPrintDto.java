package com.company.dto.room;

import com.company.model.RoomStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomPrintDto {
    private Integer id;
    private Integer cost;
    private RoomStatus status;
    private Integer roomNumber;
}