package com.company.service.impl;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.model.*;
import com.company.repository.BookingRepository;
import com.company.service.BookingService;
import com.company.service.UserService;
import com.company.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final LocalDateTime currentDate = LocalDateTime.now();
    private static final Logger log = LogManager.getLogger(BookingServiceImpl.class);

    public BookingServiceImpl(BookingRepository bookingRepository, RoomService roomService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.userService = userService;
    }

    @Override
    public void saveBooking(BookingDto bookingDto) {
        checkRightsToSaveOrUpdateOrDelete(bookingDto.getVisitorId());
        Booking booking = bookingDtoToBooking(bookingDto);
        getListByRoomId(booking.getRoomId()).forEach(bookingInList ->
                checkDateInNewBookingBeingLocatedInAreaOfLocalDateTimeInOldBooking(booking, bookingInList));
        //заселение должно быть раньше выселения, заселение должно быть после currentDate
        if (booking.getCheckInTime().isAfter(booking.getCheckOutTime()) || booking.getCheckInTime().isBefore(currentDate)) {
            log.error("Wrong date");
            throw new CustomException("wrong date");
        } else {
            //проверка, есть ли такие Id в базе
            roomService.checkRoomId(booking.getRoomId());
            userService.checkUserId(booking.getVisitorId());
            //статус комнаты должен быть AVAILABLE
            if (roomService.getById(booking.getRoomId()).getStatus().equals(RoomStatus.AVAILABLE)) {
                //сохранение услуги
                Booking newBooking = bookingRepository.save(booking);
                log.info(String.format("Create booking with Id [%d]", newBooking.getId()));
            } else {
                log.error("Wrong roomStatus");
                throw new CustomException("Номер недоступен для бронирования");
            }
        }
    }

    @Override
    public void update(int bookingId, BookingDto bookingDto) {
        checkRightsToSaveOrUpdateOrDelete(bookingDto.getVisitorId());
        Booking booking = bookingDtoToBooking(bookingDto);
        booking.setId(bookingId);
        booking.setCanceled(bookingDto.getCanceled());
        //если бронь отменена, данные из базы данных нельзя править
        if (!booking.getCanceled()) {
            getListByRoomId(booking.getRoomId()).forEach(bookingInList ->
                    checkDateInNewBookingBeingLocatedInAreaOfLocalDateTimeInOldBooking(booking, bookingInList));
            //заселение должно быть раньше выселения, заселение должно быть после currentDate
            if (booking.getCheckInTime().isAfter(booking.getCheckOutTime()) || booking.getCheckInTime().isBefore(currentDate)) {
                log.error("Wrong date");
                throw new CustomException("wrong date");
            } else {
                //проверка, есть ли такие Id в базе
                checkBookingId(bookingId);
                roomService.checkRoomId(booking.getRoomId());
                userService.checkUserId(booking.getVisitorId());
                //статус комнаты должен быть AVAILABLE
                if (roomService.getById(booking.getRoomId()).getStatus().equals(RoomStatus.AVAILABLE)) {
                    //обновление услуги
                    Booking newBooking = bookingRepository.save(booking);
                    log.info(String.format("Update booking with Id [%d]", newBooking.getId()));
                } else {
                    log.error("Wrong roomStatus");
                    throw new CustomException("Номер недоступен для бронирования");
                }
            }
        }
    }

    @Override
    public List<BookingPrintDto> getAllBooking() {
        User user = userService.getCurrentUser();
        //для админа - все брони, для юзера - только брони по ID юзера
        if (user.getRoles().stream().anyMatch(role -> RoleEnum.stringToRoleEnum(role.getName()).equals(RoleEnum.ROLE_ADMIN))) {
            return bookingToBookingPrintDto();
        } else {
            return bookingToBookingPrintDto().stream().filter(bookingPrintDto -> bookingPrintDto.getId().equals(user.getId())).collect(Collectors.toList());
        }
    }

    @Override
    public void delete(int id) {
        checkRightsToSaveOrUpdateOrDelete(getById(id).getVisitorId());
        System.out.println("ads");
        bookingRepository.delete(getById(id));
    }

    private void checkRightsToSaveOrUpdateOrDelete(Integer userId) {
        if (!userId.equals(userService.getCurrentUser().getId()) && userService.getCurrentUser().getRoles().stream().noneMatch(role -> RoleEnum.stringToRoleEnum(role.getName()).equals(RoleEnum.ROLE_ADMIN))) {
            log.error("save or update or delete this booking can only Admin or user with name " + userService.getCurrentUser().getName());
            throw new CustomException("not enough rights");
        }
    }

    private Booking bookingDtoToBooking(BookingDto booking) {
        int roomId = booking.getRoomId();
        int visitorId = booking.getVisitorId();
        LocalDate checkInTime = booking.getCheckInTime();
        LocalDate checkOutTime = booking.getCheckOutTime();
        System.out.println(new Booking(roomId, visitorId, checkInTime, checkOutTime));
        return new Booking(roomId, visitorId, checkInTime, checkOutTime);
    }

    private List<BookingPrintDto> bookingToBookingPrintDto() {
        Map<Integer, String> userNameMap = new HashMap<>();
        Map<Integer, Integer> roomNumberMap = new HashMap<>();
        userService.getAllUser().forEach(user -> userNameMap.put(user.getId(), user.getName()));
        roomService.getAllRoom().forEach(room -> roomNumberMap.put(room.getId(), room.getRoomNumber()));
        List<BookingPrintDto> bookingPrintDtoList = new ArrayList<>();
        bookingRepository.findAll().forEach(booking -> bookingPrintDtoList.add(BookingPrintDto.builder().id(booking.getId()).roomId(booking.getRoomId()).roomNumber(roomNumberMap.get(booking.getRoomId()))
                .visitorId(booking.getVisitorId()).visitorName(userNameMap.get(booking.getVisitorId())).canceled(booking.getCanceled()).checkInTime(booking.getCheckInTime()).checkOutTime(booking.getCheckOutTime()).build()));
        return bookingPrintDtoList;
    }

    private Booking getById(int id) {
        try {
            Booking booking = bookingRepository.getById(id);
            log.info("get" + booking);
            return booking;
        } catch (EntityNotFoundException e){
            throw new WrongIdException("Incorrect userId:" + id);
        }
    }

    private List<Booking> getListByRoomId(int id) {
        return bookingRepository.getBookingListByRoomId(id).stream().filter(booking -> !booking.getCanceled()).collect(Collectors.toList());
    }

    private void checkDateInNewBookingBeingLocatedInAreaOfLocalDateTimeInOldBooking(Booking newBooking, Booking booking) {
        if (!booking.getId().equals(newBooking.getId())) {
            if (newBooking.getCheckInTime().isAfter(booking.getCheckInTime()) && newBooking.getCheckInTime().isBefore(booking.getCheckOutTime())
                    || newBooking.getCheckInTime().isEqual(booking.getCheckInTime())) {
                log.error("date unavailable");
                throw new CustomException("date unavailable");
            }
            if (newBooking.getCheckOutTime().isAfter(booking.getCheckInTime()) && newBooking.getCheckOutTime().isBefore(booking.getCheckOutTime())
                    || newBooking.getCheckOutTime().isEqual(booking.getCheckInTime())) {
                log.error("date unavailable");
                throw new CustomException("date unavailable");
            }
        }
    }

    //проверка на наличие Id в базе данных
    private void checkBookingId(int bookingId) {
        getById(bookingId);
    }
}
