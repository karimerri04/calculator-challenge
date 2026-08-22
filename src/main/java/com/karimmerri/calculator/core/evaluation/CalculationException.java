package com.karimmerri.calculator.core.evaluation;

public final class CalculationException extends RuntimeException {

    public CalculationException(String message) {
        super(message);
    }

    public static CalculationException divisionByZero() {
        return new CalculationException("Division by zero is not allowed");
    }

    public static CalculationException negativeSquareRoot() {
        return new CalculationException(
                "Square root is undefined for negative numbers"
        );
    }

    public static CalculationException nonIntegerExponent() {
        return new CalculationException(
                "Only integer exponents are supported"
        );
    }

    public static CalculationException exponentOutOfRange() {
        return new CalculationException(
                "Exponent is outside the supported range [-10000, 10000]"
        );
    }
}
