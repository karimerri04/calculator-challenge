package com.karimmerri.calculator.application;

import com.karimmerri.calculator.core.evaluation.CalculationException;
import com.karimmerri.calculator.core.parser.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @ParameterizedTest
    @CsvSource({
            "'1 + 2', '3'",
            "'1 + -1', '0'",
            "'-1 - -1', '0'",
            "'5 - 4', '1'",
            "'5 * 2', '10'",
            "'(2 + 5) * 3', '21'",
            "'10 / 2', '5'",
            "'2 + 2 * 5 + 5', '17'",
            "'2.8 * 3 - 1', '7.4'",
            "'2^8', '256'",
            "'2^8*5-1', '1279'",
            "'sqrt(4)', '2'",
            "'2^3^2', '512'",
            "'-2^2', '-4'",
            "'(-2)^2', '4'"
    })
    void shouldCalculateSupportedExpressions(
            String expression,
            String expected
    ) {
        assertThat(calculator.calculate(expression))
                .isEqualByComparingTo(expected);
    }

    @Test
    void shouldRejectUnknownFunction() {
        assertThatThrownBy(() -> calculator.calculate("sin(1)"))
                .isInstanceOf(ParserException.class)
                .hasMessageContaining("Unknown function");
    }

    @Test
    void shouldRejectSquareRootOfNegativeNumber() {
        assertThatThrownBy(() -> calculator.calculate("sqrt(-1)"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Square root is undefined for negative numbers");
    }

    @Test
    void shouldRejectDivisionByZero() {
        assertThatThrownBy(() -> calculator.calculate("1/0"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Division by zero is not allowed");
    }
}
