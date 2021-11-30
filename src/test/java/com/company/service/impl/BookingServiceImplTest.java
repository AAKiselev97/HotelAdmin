package com.company.service.impl;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.dto.room.RoomPrintDto;
import com.company.dto.user.UserPrintDto;
import com.company.exception.CustomException;
import com.company.model.*;
import com.company.repository.BookingRepository;
import com.company.service.RoomService;
import com.company.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Booking booking1 = new Booking(1, 2, 1, true, LocalDateTime.now(), LocalDateTime.MAX);
    private final Booking booking2 = new Booking(2, 2, 2, false,
            LocalDateTime.of(2022, 8, 24, 14, 0), LocalDateTime.of(2022, 9, 24, 12, 0));
    private final Booking booking3 = new Booking(3, 3, 3, false,
            LocalDateTime.of(2022, 8, 26, 14, 0), LocalDateTime.of(2022, 9, 22, 12, 0));
    private final BookingPrintDto bookingPrintDto1 = BookingPrintDto.builder().id(booking1.getId()).roomId(booking1.getRoomId())
            .roomNumber(2).visitorId(booking1.getVisitorId()).visitorName("test1").canceled(true).checkInTime(booking1.getCheckInTime())
            .checkOutTime(booking1.getCheckOutTime()).build();
    private final BookingPrintDto bookingPrintDto2 = BookingPrintDto.builder().id(booking2.getId()).roomId(booking2.getRoomId())
            .roomNumber(2).visitorId(booking2.getVisitorId()).visitorName("test2").canceled(false).checkInTime(booking2.getCheckInTime())
            .checkOutTime(booking2.getCheckOutTime()).build();
    private final BookingPrintDto bookingPrintDto3 = BookingPrintDto.builder().id(booking3.getId()).roomId(booking3.getRoomId())
            .roomNumber(3).visitorId(booking3.getVisitorId()).visitorName("test3").canceled(false).checkInTime(booking3.getCheckInTime())
            .checkOutTime(booking3.getCheckOutTime()).build();
    private final User user = new User(1, "test1", "testname1", "testpassword1", Collections.singleton(new Role(1, RoleEnum.ROLE_USER.name())));
    private final User admin = new User(2, "test2", "testname2", "testpassword2", Collections.singleton(new Role(2, RoleEnum.ROLE_ADMIN.name())));
    private final BookingDto bookingDto1 = new BookingDto(2, 1, false, LocalDate.of(2022, 8, 24), LocalDate.of(2022, 8, 25));
    private final BookingDto bookingDto2 = new BookingDto(2, 2, false, LocalDate.of(2022, 8, 22), LocalDate.of(2022, 8, 24));
    private final BookingDto bookingDtoWrongDate = new BookingDto(2, 2, false, LocalDate.of(2022, 8, 25), LocalDate.of(2022, 8, 24));

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveBookingWhenAllValid() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.getBookingListByRoomId(2)).thenReturn(Arrays.asList(booking3));
        when(roomService.getById(2)).thenReturn(new Room(1, 200, RoomStatus.AVAILABLE, 2));
        when(userService.getById(1)).thenReturn(user);
        Booking bookingInService = new Booking(bookingDto1.getRoomId(), bookingDto1.getVisitorId(), bookingDto1.getCheckInTime(), bookingDto1.getCheckOutTime());
        Booking bookingInRepositories = new Booking(1, bookingDto1.getRoomId(), bookingDto1.getVisitorId(), false, bookingInService.getCheckInTime(), bookingInService.getCheckOutTime());
        when(bookingRepository.save(bookingInService)).thenReturn(bookingInRepositories);
        bookingService.saveBooking(bookingDto1);
    }

    @Test
    void saveBookingWhenNotEnoughRights() {
        when(userService.getCurrentUser()).thenReturn(user);
        assertThrows(CustomException.class, () -> bookingService.saveBooking(bookingDto2));
    }

    @Test
    void saveBookingWhenCheckInAfterCheckOut() {
        when(userService.getCurrentUser()).thenReturn(admin);
        assertThrows(CustomException.class, () -> bookingService.saveBooking(bookingDtoWrongDate));
    }

    @Test
    void saveBookingWhenCheckInBeforeCurrentDate() {
        when(userService.getCurrentUser()).thenReturn(admin);
        BookingDto bookingDto = bookingDtoWrongDate;
        bookingDtoWrongDate.setCheckInTime(LocalDate.MIN);
        assertThrows(CustomException.class, () -> bookingService.saveBooking(bookingDto));
    }

    @Test
    void saveBookingWhenRoomStatusUnavailable() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(roomService.getById(2)).thenReturn(new Room(1, RoomStatus.REPAIRABLE, 1));
        assertThrows(CustomException.class, () -> bookingService.saveBooking(bookingDto2));
    }

    @Test
    void saveBookingWhenRoomIsOccupied() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(roomService.getById(2)).thenReturn(new Room(1, 200, RoomStatus.AVAILABLE, 2));
        when(bookingRepository.getBookingListByRoomId(2)).thenReturn(Arrays.asList(booking1, booking2, booking3));
        assertThrows(CustomException.class, () -> bookingService.saveBooking(bookingDto1));
    }

    @Test
    void updateBookingWhenNotEnoughRights() {
        when(userService.getCurrentUser()).thenReturn(user);
        assertThrows(CustomException.class, () -> bookingService.update(2, bookingDto2));
    }

    @Test
    void updateBookingWhenCheckInAfterCheckOut() {
        when(userService.getCurrentUser()).thenReturn(admin);
        assertThrows(CustomException.class, () -> bookingService.update(3, bookingDtoWrongDate));
    }

    @Test
    void updateBookingWhenCheckInBeforeCurrentDate() {
        when(userService.getCurrentUser()).thenReturn(admin);
        BookingDto bookingDto = bookingDtoWrongDate;
        bookingDtoWrongDate.setCheckInTime(LocalDate.MIN);
        assertThrows(CustomException.class, () -> bookingService.update(3, bookingDto));
    }

    @Test
    void updateBookingWhenRoomStatusUnavailable() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(roomService.getById(2)).thenReturn(new Room(1, RoomStatus.REPAIRABLE, 1));
        when(bookingRepository.getById(2)).thenReturn(booking2);
        assertThrows(CustomException.class, () -> bookingService.update(2, bookingDto2));
    }

    @Test
    void updateBookingWhenRoomIsOccupied() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(roomService.getById(2)).thenReturn(new Room(1, 200, RoomStatus.AVAILABLE, 2));
        when(bookingRepository.getBookingListByRoomId(2)).thenReturn(Arrays.asList(booking1, booking2, booking3));
        assertThrows(CustomException.class, () -> bookingService.update(1, bookingDto1));
    }


    @Test
    void getAllBookingForAdmin() {
        when(userService.getCurrentUser()).thenReturn(admin);
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1, booking2, booking3));
        when(userService.getAllUser()).thenReturn(Arrays.asList(UserPrintDto.builder().id(1).name("test1").build(),
                UserPrintDto.builder().id(2).name("test2").build(), UserPrintDto.builder().id(3).name("test3").build()));
        when(roomService.getAllRoom()).thenReturn(Arrays.asList(RoomPrintDto.builder().id(1).roomNumber(1).build(),
                RoomPrintDto.builder().id(2).roomNumber(2).build(), RoomPrintDto.builder().id(3).roomNumber(3).build()));
        assertThat(bookingService.getAllBooking()).isEqualTo(Arrays.asList(bookingPrintDto1, bookingPrintDto2, bookingPrintDto3));
    }

    @Test
    void getAllBookingForUser() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(booking1, booking2, booking3));
        when(userService.getAllUser()).thenReturn(Arrays.asList(UserPrintDto.builder().id(1).name("test1").build(),
                UserPrintDto.builder().id(2).name("test2").build(), UserPrintDto.builder().id(3).name("test3").build()));
        when(roomService.getAllRoom()).thenReturn(Arrays.asList(RoomPrintDto.builder().id(1).roomNumber(1).build(),
                RoomPrintDto.builder().id(2).roomNumber(2).build(), RoomPrintDto.builder().id(3).roomNumber(3).build()));
        assertThat(bookingService.getAllBooking()).isEqualTo(Arrays.asList(bookingPrintDto1));
    }

    @Test
    void deleteWhenNotEnoughRights() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingRepository.getById(2)).thenReturn(booking3);
        assertThrows(CustomException.class, () -> bookingService.delete(2));
    }
}