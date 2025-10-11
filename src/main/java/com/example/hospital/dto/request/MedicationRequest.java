package com.example.hospital.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationRequest {

    @NotBlank(message = "Medication name is required")
    private String name;

    @NotBlank(message = "Medication code is required")
    private String code;

    @NotNull(message = "Quantity available is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantityAvailable;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}
