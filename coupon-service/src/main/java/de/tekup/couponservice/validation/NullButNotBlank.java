package de.tekup.couponservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = de.tekup.couponservice.validation.impl.NullButNotBlank.class)
public @interface NullButNotBlank {
    String message() default "field shouldn't be empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
