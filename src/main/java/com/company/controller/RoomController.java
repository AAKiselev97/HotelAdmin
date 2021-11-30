package com.company.controller;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/room")
public class RoomController {
    private final RoomService roomService;
    private static final Logger log = LogManager.getLogger(RoomController.class);

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomPrintDto>> getAllRoom() {
        List<RoomPrintDto> roomPrintDtoList = roomService.getAllRoom();
        return roomPrintDtoList != null
                ? new ResponseEntity<>(roomPrintDtoList, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomDto room) {
        //действие - создать бронь
        log.info(String.format("Try create room with cost[%s], status[%s], roomNumber[%d]",
                room.getCost(), room.getStatus(), room.getRoomNumber()));
        try {
            roomService.saveRoom(room);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable(name = "id") Integer roomId, @RequestBody RoomDto room) {
        //действие - обновить бронь
        log.info(String.format("Try update room with cost[%s], status[%s], roomNumber[%d] ID[%d]",
                room.getCost(), room.getStatus(), room.getRoomNumber(), roomId));
        try {
            roomService.update(roomId, room);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable(name = "id") int id) {
        try {
            roomService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }
}