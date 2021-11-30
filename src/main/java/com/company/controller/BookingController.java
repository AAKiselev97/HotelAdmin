package com.company.controller;

import com.company.dto.booking.BookingDto;
import com.company.dto.booking.BookingPrintDto;
import com.company.service.BookingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/client/booking")
public class BookingController {
    private final BookingService bookingService;
    private static final Logger log = LogManager.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingPrintDto>> getAllBooking() {
        List<BookingPrintDto> bookingPrintDtoList = bookingService.getAllBooking();
        return bookingPrintDtoList != null
                ? new ResponseEntity<>(bookingPrintDtoList, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDto booking) {
        //действие - создать бронь
        log.info("Try create " + booking);
        try {
            bookingService.saveBooking(booking);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable(name = "id") Integer bookingId, @Valid @RequestBody BookingDto booking) {
        //действие - обновить бронь
        log.info("Try update " + booking + "[ID]" + bookingId);
        try {
            bookingService.update(bookingId, booking);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable(name = "id") int id) {
        try {
            bookingService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
