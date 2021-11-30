package com.company.service;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.model.Room;

import java.util.List;

public interface RoomService {

    void saveRoom(RoomDto roomDto);

    void update(int id, RoomDto roomDto);

    List<RoomPrintDto> getAllRoom();

    Room getById(int id);

    void delete(int id);

    void checkRoomId(int roomId);
}
