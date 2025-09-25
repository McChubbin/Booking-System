package com.cottage.reservation.controller;

import com.cottage.reservation.dto.ReservationRequest;
import com.cottage.reservation.entity.Reservation;
import com.cottage.reservation.security.UserPrincipal;
import com.cottage.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest reservationRequest,
                                               Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Reservation reservation = reservationService.createReservation(reservationRequest, userPrincipal.getId());
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Reservation> reservations = reservationService.getReservationsByUser(userPrincipal.getId());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllActiveReservations() {
        // This endpoint shows all active reservations (for viewing other users' reservations)
        List<Reservation> reservations = reservationService.getAllActiveReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id,
                                               @Valid @RequestBody ReservationRequest reservationRequest,
                                               Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Reservation reservation = reservationService.updateReservation(id, reservationRequest, userPrincipal.getId());
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            reservationService.cancelReservation(id, userPrincipal.getId());
            return ResponseEntity.ok().body("Reservation cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<Reservation>> getReservationCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Reservation> reservations = reservationService.getReservationsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(reservations);
    }
}
