package com.company.mapper;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.model.Booking;
import com.company.model.Room;
import com.company.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BookingMapperUtil {
    public static Booking bookingDtoToBooking(BookingDto booking) {
        int roomId = booking.getRoomId();
        int visitorId = booking.getVisitorId();
        LocalDate checkInTime = booking.getCheckInTime();
        LocalDate checkOutTime = booking.getCheckOutTime();
        return new Booking(roomId, visitorId, checkInTime, checkOutTime);
    }

    public static BookingPrintDto bookingToBookingPrintDto(Booking booking, Room room, User user) {
        return BookingPrintDto.builder().id(booking.getId()).roomId(booking.getRoomId()).roomNumber(room.getRoomNumber())
                .visitorId(booking.getVisitorId()).visitorName(user.getName()).canceled(booking.getCanceled()).
                checkInTime(booking.getCheckInTime()).checkOutTime(booking.getCheckOutTime()).source(booking.getSource()).build();
    }

    public static Booking bookingPrintDtoToBooking(BookingPrintDto bookingPrintDto) {
        return new Booking(bookingPrintDto.getId(), bookingPrintDto.getRoomId(), bookingPrintDto.getVisitorId(), bookingPrintDto.getCanceled(),
                bookingPrintDto.getCheckInTime(), bookingPrintDto.getCheckOutTime(), bookingPrintDto.getSource());
    }

    public static List<BookingPrintDto> bookingListToBookingPrintDtoList(List<Booking> bookingList, Map<Integer, String> userNameMap, Map<Integer, Integer> roomNumberMap) {
        return bookingList.stream().map(booking -> BookingPrintDto.builder().id(booking.getId()).roomId(booking.getRoomId()).roomNumber(roomNumberMap.get(booking.getRoomId()))
                .visitorId(booking.getVisitorId()).visitorName(userNameMap.get(booking.getVisitorId())).canceled(booking.getCanceled()).checkInTime(booking.getCheckInTime()).
                checkOutTime(booking.getCheckOutTime()).build()).collect(Collectors.toList());
    }
}
