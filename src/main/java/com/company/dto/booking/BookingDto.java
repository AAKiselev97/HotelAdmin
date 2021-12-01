package com.company.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Validated
public class BookingDto {
    @NotNull
    private Integer roomId;
    @NotNull
    private Integer visitorId;
    private Boolean canceled;
    @NotNull
    private LocalDate checkInTime;
    @NotNull
    private LocalDate checkOutTime;
}
