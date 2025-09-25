package com.cottage.reservation.service;

import com.cottage.reservation.dto.ReservationRequest;
import com.cottage.reservation.entity.Reservation;
import com.cottage.reservation.entity.Room;
import com.cottage.reservation.entity.User;
import com.cottage.reservation.repository.ReservationRepository;
import com.cottage.reservation.repository.RoomRepository;
import com.cottage.reservation.repository.UserRepository;
import com.cottage.reservation.service.InputValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private InputValidationService inputValidationService;

    public Reservation createReservation(ReservationRequest request, Long userId) {
        // Validate input parameters
        inputValidationService.validateId(userId);
        inputValidationService.validateBean(request);
        
        // Sanitize notes if present
        if (request.getNotes() != null) {
            request.setNotes(inputValidationService.validateAndSanitizeText(request.getNotes()));
        }
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate room exists and is available
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getIsAvailable()) {
            throw new RuntimeException("Room is not available");
        }

        // Validate dates
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }

        if (request.getCheckOutDate().isBefore(request.getCheckInDate().plusDays(1))) {
            throw new RuntimeException("Check-out date must be at least one day after check-in date");
        }

        // Check for conflicting reservations
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Set total price to zero for free reservations
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Create reservation
        Reservation reservation = new Reservation(
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getNumberOfGuests(),
                totalPrice,
                user,
                room);
        
        reservation.setNotes(request.getNotes());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);

        // Send confirmation email
        emailService.sendReservationConfirmation(savedReservation);

        return savedReservation;
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByCheckInDateDesc(userId);
    }

    public List<Reservation> getAllActiveReservations() {
        return reservationRepository.findActiveReservations();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation updateReservation(Long reservationId, ReservationRequest request, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check if user owns the reservation
        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own reservations");
        }

        // Check if reservation can be updated (not completed or cancelled)
        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED ||
            reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cannot update completed or cancelled reservations");
        }

        // Validate new dates
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }

        if (request.getCheckOutDate().isBefore(request.getCheckInDate().plusDays(1))) {
            throw new RuntimeException("Check-out date must be at least one day after check-in date");
        }

        // Check for conflicts excluding current reservation
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        conflicts.removeIf(r -> r.getId().equals(reservationId));
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Update reservation
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Set total price to zero for free reservations
        BigDecimal totalPrice = BigDecimal.ZERO;

        reservation.setRoom(room);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setTotalPrice(totalPrice);
        reservation.setNotes(request.getNotes());

        Reservation updatedReservation = reservationRepository.save(reservation);

        // Send update email
        emailService.sendReservationUpdate(updatedReservation);

        return updatedReservation;
    }

    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check if user owns the reservation
        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own reservations");
        }

        // Check if reservation can be cancelled
        if (reservation.getStatus() == Reservation.ReservationStatus.COMPLETED ||
            reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel completed or already cancelled reservations");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Send cancellation email
        emailService.sendReservationCancellation(reservation);
    }

    public List<Reservation> getReservationsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findReservationsBetweenDates(startDate, endDate);
    }
}
