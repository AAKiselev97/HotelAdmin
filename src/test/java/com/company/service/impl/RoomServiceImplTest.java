package com.company.service.impl;

import com.company.dto.room.RoomDto;
import com.company.dto.room.RoomPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.model.Room;
import com.company.model.RoomStatus;
import com.company.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private final Room room1 = new Room(1, 100, RoomStatus.AVAILABLE, 1);
    private final Room room2 = new Room(2, 200, RoomStatus.AVAILABLE, 2);
    private final Room room3 = new Room(3, 300, RoomStatus.AVAILABLE, 3);
    private final RoomPrintDto printDto1 = RoomPrintDto.builder().id(room1.getId()).cost(room1.getCost()).status(room1.getStatus()).roomNumber(room1.getRoomNumber()).build();
    private final RoomPrintDto printDto2 = RoomPrintDto.builder().id(room2.getId()).cost(room2.getCost()).status(room2.getStatus()).roomNumber(room2.getRoomNumber()).build();
    private final RoomPrintDto printDto3 = RoomPrintDto.builder().id(room3.getId()).cost(room3.getCost()).status(room3.getStatus()).roomNumber(room3.getRoomNumber()).build();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveRoomWhenRoomNumberEmployed() {
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomNumber(1);
        assertThrows(CustomException.class, () -> roomService.saveRoom(roomDto));
    }

    @Test
    void updateRoomWhenRoomNumberEmployed() {
        when(roomRepository.getById(1)).thenReturn(room1);
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomNumber(2);
        assertThrows(CustomException.class, () -> roomService.update(1, roomDto));
    }

    @Test
    void updateWhenRepositoriesDoesntHaveId() {
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomNumber(2);
        assertThrows(WrongIdException.class, () -> roomService.update(-1, roomDto));
    }

    @Test
    void getAllRoom() {
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));
        assertThat(roomService.getAllRoomPrintDto()).isEqualTo(Arrays.asList(printDto1, printDto2, printDto3));
    }

    @Test
    void getByValidId() {
        when(roomRepository.getById(3)).thenReturn(room1);
        assertThat(roomService.getById(3)).isEqualTo(room1);
    }

    @Test
    void getByInvalidId() {
        assertThrows(WrongIdException.class, () -> roomService.getById(1));
    }
}
