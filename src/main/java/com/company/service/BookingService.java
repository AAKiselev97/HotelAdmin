package com.company.service;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;

import java.util.List;

public interface BookingService {

    void saveBooking(BookingDto booking);

    List<BookingPrintDto> getAllBooking();

    void update(int id, BookingDto booking);

    void delete(int id);
}
