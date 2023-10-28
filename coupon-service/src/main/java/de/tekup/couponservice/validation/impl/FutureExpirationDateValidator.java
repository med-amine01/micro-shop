package de.tekup.couponservice.validation.impl;

import de.tekup.couponservice.validation.FutureExpirationDate;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Slf4j
public class FutureExpirationDateValidator implements ConstraintValidator<FutureExpirationDate, String> {
    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // Null values are handled by the @NotNull annotation
        }
        
        try {
            LocalDate expirationDate = LocalDate.parse(date.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return expirationDate.isAfter(LocalDate.now());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false; // Invalid date format
        }
    }
}
