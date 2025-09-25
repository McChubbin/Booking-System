package com.cottage.reservation.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class InputSanitizer {
    
    // Patterns for potentially dangerous content
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i).*(union|select|insert|update|delete|drop|create|alter|exec|execute|--|'|;|\\||xp_)", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i).*(<script|</script|javascript:|vbscript:|onload=|onerror=|<iframe|<object|<embed)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    
    /**
     * Sanitizes text input by removing potentially dangerous characters
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        
        // Trim whitespace
        String sanitized = input.trim();
        
        // Remove HTML tags
        sanitized = HTML_TAG_PATTERN.matcher(sanitized).replaceAll("");
        
        // Escape special characters that could be used in SQL injection
        sanitized = sanitized.replace("'", "''")
                           .replace("\"", "&quot;")
                           .replace("<", "&lt;")
                           .replace(">", "&gt;")
                           .replace("&", "&amp;");
        
        return sanitized;
    }
    
    /**
     * Validates that text doesn't contain SQL injection patterns
     * @param input The input to validate
     * @return true if safe, false if potentially dangerous
     */
    public boolean isSqlSafe(String input) {
        if (input == null) {
            return true;
        }
        return !SQL_INJECTION_PATTERN.matcher(input).matches();
    }
    
    /**
     * Validates that text doesn't contain XSS patterns
     * @param input The input to validate
     * @return true if safe, false if potentially dangerous
     */
    public boolean isXssSafe(String input) {
        if (input == null) {
            return true;
        }
        return !XSS_PATTERN.matcher(input).matches();
    }
    
    /**
     * Sanitizes input for use in search queries
     * @param searchTerm The search term to sanitize
     * @return Sanitized search term
     */
    public String sanitizeSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }
        
        // Remove potentially dangerous characters for search
        return searchTerm.replaceAll("['\";\\-\\-\\/\\*\\+\\|\\\\]", "")
                        .trim()
                        .substring(0, Math.min(searchTerm.length(), 100)); // Limit length
    }
    
    /**
     * Validates and sanitizes a numeric string
     * @param input The input string
     * @return Sanitized numeric string or null if invalid
     */
    public String sanitizeNumeric(String input) {
        if (input == null) {
            return null;
        }
        
        // Only allow digits, decimal point, and minus sign
        String sanitized = input.replaceAll("[^0-9.-]", "");
        
        // Validate it's a proper number format
        try {
            Double.parseDouble(sanitized);
            return sanitized;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Sanitizes email input
     * @param email The email to sanitize
     * @return Sanitized email
     */
    public String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }
        
        // Convert to lowercase and trim
        String sanitized = email.toLowerCase().trim();
        
        // Remove any potentially dangerous characters except those valid in emails
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9@._-]", "");
        
        return sanitized;
    }
}
