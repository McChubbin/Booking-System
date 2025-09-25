package com.cottage.reservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SafeTextValidator implements ConstraintValidator<SafeText, String> {
    
    // Patterns to detect potential SQL injection and XSS attempts
    private static final Pattern[] DANGEROUS_PATTERNS = {
        Pattern.compile("(?i).*('|(\\-\\-)|(;)|(\\||\\|)|(\\*|\\*))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(union|select|insert|update|delete|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(<script|</script|javascript:|vbscript:|onload=|onerror=)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i).*(<iframe|<object|<embed|<applet)", Pattern.CASE_INSENSITIVE)
    };
    
    private int maxLength;
    private boolean allowHtml;
    
    @Override
    public void initialize(SafeText constraintAnnotation) {
        this.maxLength = constraintAnnotation.maxLength();
        this.allowHtml = constraintAnnotation.allowHtml();
    }
    
    @Override
    public boolean isValid(String text, ConstraintValidatorContext context) {
        if (text == null) {
            return true; // Let @NotNull handle null validation
        }
        
        // Check length
        if (text.length() > maxLength) {
            return false;
        }
        
        // If HTML is not allowed, check for dangerous patterns
        if (!allowHtml) {
            for (Pattern pattern : DANGEROUS_PATTERNS) {
                if (pattern.matcher(text).matches()) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
