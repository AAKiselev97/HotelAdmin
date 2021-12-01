package com.company.service.impl;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.mapper.RoomMapperUtil;
import com.company.model.Room;
import com.company.repository.RoomRepository;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private static final Logger log = LogManager.getLogger(RoomServiceImpl.class);

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveRoom(RoomDto roomDto) {
        Room room = RoomMapperUtil.roomDtoToRoom(roomDto);
        if (getAllRoomPrintDto().stream().anyMatch(roomInCache -> roomInCache.getRoomNumber().equals(room.getRoomNumber()))) {
            log.info("Комната с таким номером уже существует");
            throw new CustomException("Комната с номером " + room.getRoomNumber() + " уже существует");
        }
        roomRepository.save(room);
        log.info("Create room with Id " + room.getId());
    }

    @Override
    public List<RoomPrintDto> getAllRoomPrintDto() {
        return RoomMapperUtil.roomToRoomPrintDto(getAll());
    }

    @Override
    public void update(int roomId, RoomDto roomDto) {
        checkRoomId(roomId);
        Room room = RoomMapperUtil.roomDtoToRoom(roomDto, roomId);
        if (getAllRoomPrintDto().stream().filter(roomInCache -> !roomInCache.getId().equals(roomId)).anyMatch(roomInCache -> roomInCache.getRoomNumber().equals(room.getRoomNumber()))) {
            log.info("Комната с таким номером уже существует");
            throw new CustomException("Комната с номером " + room.getRoomNumber() + " уже существует");
        }
        roomRepository.save(room);
        log.info("Update room with Id " + room.getId());

    }

    public Room getById(int id) {
        try {
            Room room = roomRepository.getById(id);
            log.info("get room by ID" + room.getId());
            return room;
        } catch (Exception e) {
            throw new WrongIdException("Incorrect userId:" + id);
        }
    }

    //проверка на наличие Id в базе данных
    public void checkRoomId(int roomId) {
        getById(roomId);
    }

    @Override
    public void delete(int id) {
        roomRepository.delete(getById(id));
    }

    private List<Room> getAll() {
        return roomRepository.findAll();
    }
}
