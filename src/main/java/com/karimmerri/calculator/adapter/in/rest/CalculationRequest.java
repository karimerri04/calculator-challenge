package com.karimmerri.calculator.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CalculationRequest(
        @NotBlank(message = "expression must not be blank")
        @Size(max = 1000, message = "expression must not exceed 1000 characters")
        String expression
) {
}
