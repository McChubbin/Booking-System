package com.cottage.reservation.service;

import com.cottage.reservation.util.InputSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InputValidationService {
    
    @Autowired
    private InputSanitizer inputSanitizer;
    
    @Autowired
    private Validator validator;
    
    /**
     * Validates and sanitizes text input
     * @param input The text to validate and sanitize
     * @return Sanitized text
     * @throws IllegalArgumentException if input contains dangerous content
     */
    public String validateAndSanitizeText(String input) {
        if (input == null) {
            return null;
        }
        
        // Check for SQL injection patterns
        if (!inputSanitizer.isSqlSafe(input)) {
            throw new IllegalArgumentException("Input contains potentially dangerous SQL patterns");
        }
        
        // Check for XSS patterns
        if (!inputSanitizer.isXssSafe(input)) {
            throw new IllegalArgumentException("Input contains potentially dangerous script patterns");
        }
        
        // Sanitize the input
        return inputSanitizer.sanitizeText(input);
    }
    
    /**
     * Validates a bean using Jakarta validation annotations
     * @param object The object to validate
     * @param <T> The type of object
     * @throws IllegalArgumentException if validation fails
     */
    public <T> void validateBean(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }
    
    /**
     * Validates and sanitizes a search term
     * @param searchTerm The search term to process
     * @return Sanitized search term
     */
    public String validateAndSanitizeSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null;
        }
        
        // Check for dangerous patterns
        if (!inputSanitizer.isSqlSafe(searchTerm) || !inputSanitizer.isXssSafe(searchTerm)) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        
        return inputSanitizer.sanitizeSearchTerm(searchTerm);
    }
    
    /**
     * Validates a numeric ID parameter
     * @param id The ID to validate
     * @throws IllegalArgumentException if ID is invalid
     */
    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: must be a positive number");
        }
    }
    
    /**
     * Validates an email address
     * @param email The email to validate
     * @return Sanitized email
     */
    public String validateAndSanitizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        String sanitized = inputSanitizer.sanitizeEmail(email);
        
        // Basic email format validation
        if (!sanitized.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return sanitized;
    }
}
