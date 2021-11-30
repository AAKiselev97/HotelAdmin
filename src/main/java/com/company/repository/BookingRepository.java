package com.company.repository;


import com.company.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.roomId = :id")
    List<Booking> getBookingListByRoomId(@Param("id") int id);

    @Query("SELECT b FROM Booking b WHERE b.visitorId = :id")
    List<Booking> findAllForUser(@Param("id") int id);
}

