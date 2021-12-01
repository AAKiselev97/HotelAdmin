package com.company.controller;

import com.company.dto.room.RoomDto;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/admin/room")
public class RoomControllerForAdmin {
    private final RoomService roomService;
    private static final Logger log = LogManager.getLogger(RoomControllerForAdmin.class);

    public RoomControllerForAdmin(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomDto room) {
        //действие - создать бронь
        log.info(String.format("Try create room with cost[%s], status[%s], roomNumber[%d]",
                room.getCost(), room.getStatus(), room.getRoomNumber()));
        roomService.saveRoom(room);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable(name = "id") Integer roomId, @RequestBody RoomDto room) {
        //действие - обновить бронь
        log.info(String.format("Try update room with cost[%s], status[%s], roomNumber[%d] ID[%d]",
                room.getCost(), room.getStatus(), room.getRoomNumber(), roomId));
        roomService.update(roomId, room);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable(name = "id") int id) {
        roomService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        return new ResponseEntity<>("Exception message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

