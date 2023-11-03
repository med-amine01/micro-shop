package de.tekup.productservice.validation.impl;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class NullButNotBlank implements ConstraintValidator<de.tekup.productservice.validation.NullButNotBlank, String> {
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (null == name) {
            return true; // Null values are accepted
        }

        return !name.trim().isEmpty(); // not empty trimmed values are accepted else return false
    }
}
