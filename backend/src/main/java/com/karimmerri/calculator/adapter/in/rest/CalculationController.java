package com.karimmerri.calculator.adapter.in.rest;

import com.karimmerri.calculator.application.Calculator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculations")
public class CalculationController {

    private final Calculator calculator;

    public CalculationController(Calculator calculator) {
        this.calculator = calculator;
    }

    @PostMapping
    public ResponseEntity<CalculationResponse> calculate(
            @Valid @RequestBody CalculationRequest request
    ) {
        var result = calculator.calculate(request.expression());

        return ResponseEntity.ok(
                new CalculationResponse(
                        request.expression(),
                        result.stripTrailingZeros().toPlainString()
                )
        );
    }
}
