package com.company.controller;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/booking")
public class BookingControllerForAdmin {
    private final BookingService bookingService;
    private static final Logger log = LogManager.getLogger(BookingControllerForAdmin.class);

    public BookingControllerForAdmin(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingPrintDto>> getAllBookingForAdmin() {
        List<BookingPrintDto> bookingPrintDtoList = bookingService.getAllBookingPrintDtoForAdmin();
        return new ResponseEntity<>(bookingPrintDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Resource> getReceipt(@PathVariable(name = "id") Integer bookingId) throws FileNotFoundException {
        return new ResponseEntity<>(bookingService.getReceiptForAdmin(bookingId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDto booking) {
        //действие - создать бронь
        log.info("Try create " + booking);
        bookingService.saveBookingForAdmin(booking);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable(name = "id") Integer bookingId, @Valid @RequestBody BookingDto booking) {
        //действие - обновить бронь
        log.info("Try update " + booking + " [ID]" + bookingId);
        bookingService.updateForAdmin(bookingId, booking);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable(name = "id") int id) {
        bookingService.deleteForAdmin(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        return new ResponseEntity<>("Exception message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
