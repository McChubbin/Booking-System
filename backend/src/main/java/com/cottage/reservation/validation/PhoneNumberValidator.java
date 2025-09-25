package com.cottage.reservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    
    private static final String PHONE_PATTERN = 
        "^(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$";
    
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);
    
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Allow null/empty for optional fields
        }
        return pattern.matcher(phoneNumber.trim()).matches();
    }
}
