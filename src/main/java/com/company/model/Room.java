package com.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "cost", nullable = false)
    private Integer cost;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    @Column(name = "number", nullable = false, unique = true)
    private Integer roomNumber;

    public Room(Integer cost, RoomStatus status, Integer roomNumber) {
        this.cost = cost;
        this.status = status;
        this.roomNumber = roomNumber;
    }
}
