package com.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "room_id", nullable = false)
    private Integer roomId;
    @Column(name = "visitor_id", nullable = false)
    private Integer visitorId;
    //отменено ли бронирование
    @Column(name = "canceled", nullable = false)
    private Boolean canceled;
    //время вселения
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;
    //время выселения
    @Column(name = "check_out_time", nullable = false)
    private LocalDateTime checkOutTime;
    @Column(name = "source")
    private String source;

    public Booking(int roomId, int visitorId, LocalDate checkInTime, LocalDate checkOutTime) {
        this.roomId = roomId;
        this.visitorId = visitorId;
        //вселение - после 14:00, выселение - до 12:00
        this.checkInTime = LocalDateTime.of(checkInTime, LocalTime.of(14, 0));
        this.checkOutTime = LocalDateTime.of(checkOutTime, LocalTime.of(12, 0));
        this.canceled = false;
    }
}
