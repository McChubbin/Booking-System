package com.cottage.reservation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SafeTextValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeText {
    String message() default "Text contains invalid characters or potential security threats";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int maxLength() default 1000;
    boolean allowHtml() default false;
}
