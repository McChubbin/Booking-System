package com.cottage.reservation.exception;

/**
 * Exception thrown when security violations are detected
 */
public class SecurityException extends RuntimeException {
    
    public SecurityException(String message) {
        super(message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
