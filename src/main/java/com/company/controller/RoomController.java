package com.company.controller;

import com.company.dto.room.RoomPrintDto;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;
    private static final Logger log = LogManager.getLogger(RoomController.class);

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomPrintDto>> getAllRoom() {
        List<RoomPrintDto> roomPrintDtoList = roomService.getAllRoomPrintDto();
        return new ResponseEntity<>(roomPrintDtoList, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        return new ResponseEntity<>("Exception message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
