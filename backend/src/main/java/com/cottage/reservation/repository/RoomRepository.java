package com.cottage.reservation.repository;

import com.cottage.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByIsAvailableTrue();
    
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE res.status IN ('PENDING', 'CONFIRMED') " +
           "AND NOT (res.checkOutDate <= :startDate OR res.checkInDate >= :endDate))")
    List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Room r WHERE r.id = :roomId AND r.isAvailable = true AND r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE res.status IN ('PENDING', 'CONFIRMED') " +
           "AND NOT (res.checkOutDate <= :startDate OR res.checkInDate >= :endDate))")
    Room findAvailableRoomById(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
