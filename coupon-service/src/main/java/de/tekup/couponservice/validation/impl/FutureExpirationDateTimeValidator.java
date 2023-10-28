package de.tekup.couponservice.validation.impl;

import de.tekup.couponservice.validation.FutureExpirationDateTime;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Slf4j
public class FutureExpirationDateTimeValidator implements ConstraintValidator<FutureExpirationDateTime, String> {
    @Override
    public boolean isValid(String dateTime, ConstraintValidatorContext context) {
        if (dateTime == null) {
            return true; // Null values are handled by the @NotNull annotation
        }
        
        try {
            LocalDateTime expirationDateTime = LocalDateTime.parse(
                    dateTime.trim(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            );

            return expirationDateTime.isAfter(LocalDateTime.now());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false; // Invalid date-time format
        }
    }
}
