package com.company.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingPrintDto {
    private Integer id;
    private Integer roomId;
    private Integer roomNumber;
    private Integer visitorId;
    private String visitorName;
    private Boolean canceled;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String source;
}
