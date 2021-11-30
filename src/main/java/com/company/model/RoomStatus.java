package com.company.model;

import com.company.exception.CustomException;

import java.util.Arrays;

public enum RoomStatus {
    AVAILABLE("доступен для бронирования"),
    REPAIRABLE("на ремонте"),
    CLEANING("идёт уборка"),
    ;

    private final String val;

    RoomStatus(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public String toString() {
        return name() + " - " + val;
    }

    public static RoomStatus stringToRoomStatus(String command) {
        return Arrays.stream(values()).filter(roomStatus -> roomStatus.name().equalsIgnoreCase(command))
                .findFirst().orElseThrow(() -> new CustomException("Wrong parsing string to room status"));
    }
}
