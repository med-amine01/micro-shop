package de.tekup.productservice.dto;

import de.tekup.productservice.validation.NullButNotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductRequestUpdate {
    
    @NullButNotBlank
    private String name;
    
    private String description;
    
    @Positive(message = "Price amount must be positive")
    private BigDecimal price;
    
    @NullButNotBlank
    private String couponCode;

    private boolean enabled;
}
