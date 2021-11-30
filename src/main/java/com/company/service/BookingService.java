package com.company.service;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import org.springframework.core.io.InputStreamResource;

import java.io.FileNotFoundException;
import java.util.List;

public interface BookingService {

    void saveBooking(BookingDto booking);

    void update(int id, BookingDto booking);

    void delete(int id);

    void saveBookingForAdmin(BookingDto booking);

    void updateForAdmin(int id, BookingDto booking);

    void deleteForAdmin(int id);

    InputStreamResource getReceipt(int id) throws FileNotFoundException;

    InputStreamResource getReceiptForAdmin(int id) throws FileNotFoundException;

    List<BookingPrintDto> getAllBookingPrintDtoForUser();

    List<BookingPrintDto> getAllBookingPrintDtoForAdmin();
}
