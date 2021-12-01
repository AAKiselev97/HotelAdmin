package com.company.service.impl;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.exception.CustomException;
import com.company.exception.WrongIdException;
import com.company.mapper.BookingMapperUtil;
import com.company.model.Booking;
import com.company.model.RoleEnum;
import com.company.model.RoomStatus;
import com.company.model.User;
import com.company.repository.BookingRepository;
import com.company.service.BookingService;
import com.company.service.RoomService;
import com.company.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final LocalDateTime currentDate = LocalDateTime.now();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final Logger log = LogManager.getLogger(BookingServiceImpl.class);

    public BookingServiceImpl(BookingRepository bookingRepository, RoomService roomService, UserService userService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public void saveBookingForAdmin(BookingDto bookingDto) {
        Booking booking = BookingMapperUtil.bookingDtoToBooking(bookingDto);
        booking.setCanceled(false);
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
                sendBookingPrintDto(booking);
            } else {
                log.error("Wrong roomStatus");
                throw new CustomException("Номер недоступен для бронирования");
            }
        }
    }

    @Override
    public void saveBooking(BookingDto bookingDto) {
        checkRightsToSaveOrUpdateOrDelete(bookingDto.getVisitorId());
        saveBookingForAdmin(bookingDto);
    }

    @Override
    public void updateForAdmin(int bookingId, BookingDto bookingDto) {
        Booking booking = BookingMapperUtil.bookingDtoToBooking(bookingDto);
        booking.setId(bookingId);
        booking.setCanceled(bookingDto.getCanceled());
        booking.setSource(getById(bookingId).getSource());
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
                    sendBookingPrintDto(booking);
                    log.info(String.format("Update booking with Id [%d]", newBooking.getId()));
                } else {
                    log.error("Wrong roomStatus");
                    throw new CustomException("Номер недоступен для бронирования");
                }
            }
        }
    }

    @Override
    public void update(int bookingId, BookingDto bookingDto) {
        checkRightsToSaveOrUpdateOrDelete(bookingDto.getVisitorId());
        updateForAdmin(bookingId, bookingDto);
    }

    @Override
    public List<BookingPrintDto> getAllBookingPrintDtoForUser() {
        User user = userService.getCurrentUser();
        return generateUserNameMapAndRoomNumberMapAndGetBookingPrintDtoList(getAllBookingByUserId(user.getId()));
    }

    @Override
    public List<BookingPrintDto> getAllBookingPrintDtoForAdmin() {
        return generateUserNameMapAndRoomNumberMapAndGetBookingPrintDtoList(getAllBooking());
    }

    @Override
    public void deleteForAdmin(int id) {
        bookingRepository.delete(getById(id));
    }

    @Override
    public void delete(int id) {
        checkRightsToSaveOrUpdateOrDelete(getById(id).getVisitorId());
        deleteForAdmin(id);
    }

    public InputStreamResource getReceipt(int id) throws FileNotFoundException {
        if (getById(id).getVisitorId().equals(userService.getCurrentUser().getId())) {
            return new InputStreamResource(new FileInputStream(restTemplate.getForObject("http://localhost:8080/receipt/" + getById(id).getSource(), File.class)));
        } else {
            log.error("Not enough rights");
            throw new CustomException("Not enough rights");
        }
    }

    public InputStreamResource getReceiptForAdmin(int id) throws FileNotFoundException {
        return new InputStreamResource(new FileInputStream(restTemplate.getForObject("http://localhost:8080/receipt/" + getById(id).getSource(), File.class)));
    }

    private void sendBookingPrintDto(Booking booking) {
        try {
            BookingPrintDto bookingPrintDto = BookingMapperUtil.bookingToBookingPrintDto(booking, roomService.getById(booking.getRoomId()), userService.getById(booking.getVisitorId()));
            System.out.println("Producing: " + booking);
            kafkaTemplate.send("createReceipt", objectMapper.writeValueAsString(bookingPrintDto));
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new CustomException(e);
        }
    }

    @KafkaListener(topics = "messages", groupId = "message_group_id")
    private void update(String message) {
        try {
            BookingPrintDto bookingPrintDto = objectMapper.readValue(message, BookingPrintDto.class);
            bookingRepository.save(BookingMapperUtil.bookingPrintDtoToBooking(bookingPrintDto));
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new CustomException(e);
        }
    }

    private void checkRightsToSaveOrUpdateOrDelete(Integer userId) {
        if (!userId.equals(userService.getCurrentUser().getId()) && userService.getCurrentUser().getRoles().stream().noneMatch(role -> RoleEnum.stringToRoleEnum(role.getName()).equals(RoleEnum.ROLE_ADMIN))) {
            log.error("save or update or delete this booking can only Admin or user with name " + userService.getCurrentUser().getName());
            throw new CustomException("not enough rights");
        }
    }

    private List<BookingPrintDto> generateUserNameMapAndRoomNumberMapAndGetBookingPrintDtoList(List<Booking> bookingList) {
        Map<Integer, String> userNameMap = new HashMap<>();
        Map<Integer, Integer> roomNumberMap = new HashMap<>();
        //для снижения количества обращений к базе данных
        bookingList.forEach(booking -> {
            if (!userNameMap.containsKey(booking.getVisitorId())) {
                userNameMap.put(booking.getVisitorId(), userService.getById(booking.getVisitorId()).getName());
            }
        });
        bookingList.forEach(booking -> {
            if (!roomNumberMap.containsKey(booking.getRoomId())) {
                roomNumberMap.put(booking.getRoomId(), roomService.getById(booking.getRoomId()).getRoomNumber());
            }
        });
        return BookingMapperUtil.bookingListToBookingPrintDtoList(bookingList, userNameMap, roomNumberMap);
    }

    private List<Booking> getAllBooking() {
        return bookingRepository.findAll();
    }

    private List<Booking> getAllBookingByUserId(int id) {
        return bookingRepository.findAllForUser(id);
    }

    private Booking getById(int id) {
        try {
            Booking booking = bookingRepository.getById(id);
            log.info("get booking by ID" + booking.getId());
            return booking;
        } catch (Exception e) {
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
                log.error("Room is occupied for this date");
                throw new CustomException("Room is occupied for this date");
            }
            if (newBooking.getCheckOutTime().isAfter(booking.getCheckInTime()) && newBooking.getCheckOutTime().isBefore(booking.getCheckOutTime())
                    || newBooking.getCheckOutTime().isEqual(booking.getCheckInTime())) {
                log.error("Room is occupied for this date");
                throw new CustomException("Room is occupied for this date");
            }
        }
    }

    //проверка на наличие Id в базе данных
    private void checkBookingId(int bookingId) {
        getById(bookingId);
    }
}
