package com.company.mapper;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.model.Room;

import java.util.List;
import java.util.stream.Collectors;

public class RoomMapperUtil {
    public static Room roomDtoToRoom(RoomDto roomDto) {
        return new Room(roomDto.getCost(), roomDto.getStatus(), roomDto.getRoomNumber());
    }

    public static Room roomDtoToRoom(RoomDto roomDto, int roomId) {
        return new Room(roomId, roomDto.getCost(), roomDto.getStatus(), roomDto.getRoomNumber());
    }

    public static List<RoomPrintDto> roomToRoomPrintDto(List<Room> roomList) {
        return roomList.stream().map(room -> RoomPrintDto.builder().id(room.getId()).cost(room.getCost()).status(room.getStatus()).roomNumber(room.getRoomNumber()).build())
                .collect(Collectors.toList());
    }
}
