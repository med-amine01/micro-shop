package de.tekup.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusRequest {
    
    @NotBlank(message = "order status is required.")
    @Pattern(regexp = "^(placed|canceled)$", message = "Invalid value. It must be placed or canceled.")
    private String orderStatus;
}
