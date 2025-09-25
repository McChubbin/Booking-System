package com.cottage.reservation.repository;

import com.cottage.reservation.entity.Reservation;
import com.cottage.reservation.entity.Reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByUserIdOrderByCheckInDateDesc(Long userId);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservations();
    
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.status IN ('PENDING', 'CONFIRMED') " +
           "AND NOT (r.checkOutDate <= :startDate OR r.checkInDate >= :endDate)")
    List<Reservation> findConflictingReservations(@Param("roomId") Long roomId, 
                                                 @Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate >= :startDate AND r.checkOutDate <= :endDate")
    List<Reservation> findReservationsBetweenDates(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY r.checkInDate ASC")
    List<Reservation> findUpcomingReservationsByUser(@Param("userId") Long userId);
}
