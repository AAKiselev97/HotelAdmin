package com.company.service.impl;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.model.Room;
import com.company.model.User;
import com.company.repository.RoomRepository;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private static final Logger log = LogManager.getLogger(RoomServiceImpl.class);

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void saveRoom(RoomDto roomDto) {
        Room room = new Room(roomDto.getCost(), roomDto.getStatus(), roomDto.getRoomNumber());
        if (getAllRoom().stream().anyMatch(roomInCache -> roomInCache.getRoomNumber().equals(room.getRoomNumber()))) {
            log.info("Комната с таким номером уже существует");
            throw new CustomException("Комната с номером " + room.getRoomNumber() + " уже существует");
        }
        Room newRoom = roomRepository.save(room);
        log.info("Create room with Id " + room.getId());
    }

    @Override
    public List<RoomPrintDto> getAllRoom() {
        //получить List комнат
        return roomToRoomPrintDto();
    }

    public List<RoomPrintDto> roomToRoomPrintDto() {
        return roomRepository.findAll().stream().map(room -> RoomPrintDto.builder().id(room.getId()).cost(room.getCost()).status(room.getStatus()).roomNumber(room.getRoomNumber()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void update(int roomId, RoomDto roomDto) {
        checkRoomId(roomId);
        Room room = new Room(roomId, roomDto.getCost(), roomDto.getStatus(), roomDto.getRoomNumber());
        if (getAllRoom().stream().filter(roomInCache -> !roomInCache.getId().equals(roomId)).anyMatch(roomInCache -> roomInCache.getRoomNumber().equals(room.getRoomNumber()))) {
            log.info("Комната с таким номером уже существует");
            throw new CustomException("Комната с номером " + room.getRoomNumber() + " уже существует");
        }
        Room newRoom = roomRepository.save(room);
        log.info("Update room with Id " + room.getId());

    }

    public Room getById(int id) {
        try {
            Room room = roomRepository.getById(id);
            log.info("get" + room);
            return room;
        } catch (EntityNotFoundException e){
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
}
