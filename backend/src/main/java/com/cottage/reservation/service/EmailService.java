package com.cottage.reservation.service;

import com.cottage.reservation.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendReservationConfirmation(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@reserveease.com");
        message.setTo(reservation.getUser().getEmail());
        message.setSubject("ReserveEase Confirmation - Reservation #" + reservation.getId());
        
        String text = String.format(
                "Dear %s,\n\n" +
                "Your reservation has been confirmed with ReserveEase!\n\n" +
                "Reservation Details:\n" +
                "- Reservation ID: %d\n" +
                "- Location: %s\n" +
                "- Check-in Date: %s\n" +
                "- Check-out Date: %s\n" +
                "- Number of Guests: %d\n" +
                "- Cost: FREE!\n" +
                "- Status: %s\n\n" +
                "%s\n\n" +
                "Thank you for choosing ReserveEase - your free reservation platform!\n\n" +
                "Best regards,\n" +
                "The ReserveEase Team",
                reservation.getUser().getFirstName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumberOfGuests(),
                reservation.getTotalPrice(),
                reservation.getStatus().getDisplayName(),
                reservation.getNotes() != null ? "Notes: " + reservation.getNotes() : ""
        );
        
        message.setText(text);
        
        try {
            emailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the reservation
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    public void sendReservationUpdate(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@reserveease.com");
        message.setTo(reservation.getUser().getEmail());
        message.setSubject("ReserveEase Update - Reservation #" + reservation.getId());
        
        String text = String.format(
                "Dear %s,\n\n" +
                "Your ReserveEase reservation has been updated.\n\n" +
                "Updated Reservation Details:\n" +
                "- Reservation ID: %d\n" +
                "- Location: %s\n" +
                "- Check-in Date: %s\n" +
                "- Check-out Date: %s\n" +
                "- Number of Guests: %d\n" +
                "- Cost: FREE!\n" +
                "- Status: %s\n\n" +
                "%s\n\n" +
                "If you have any questions, please contact us.\n\n" +
                "Best regards,\n" +
                "The ReserveEase Team",
                reservation.getUser().getFirstName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumberOfGuests(),
                reservation.getTotalPrice(),
                reservation.getStatus().getDisplayName(),
                reservation.getNotes() != null ? "Notes: " + reservation.getNotes() : ""
        );
        
        message.setText(text);
        
        try {
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send update email: " + e.getMessage());
        }
    }

    public void sendReservationCancellation(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@reserveease.com");
        message.setTo(reservation.getUser().getEmail());
        message.setSubject("ReserveEase Cancellation - Reservation #" + reservation.getId());
        
        String text = String.format(
                "Dear %s,\n\n" +
                "Your ReserveEase reservation has been cancelled.\n\n" +
                "Cancelled Reservation Details:\n" +
                "- Reservation ID: %d\n" +
                "- Location: %s\n" +
                "- Check-in Date: %s\n" +
                "- Check-out Date: %s\n\n" +
                "If you have any questions about this cancellation, please contact us.\n\n" +
                "Best regards,\n" +
                "The ReserveEase Team",
                reservation.getUser().getFirstName(),
                reservation.getId(),
                reservation.getRoom().getName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate()
        );
        
        message.setText(text);
        
        try {
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }
}
