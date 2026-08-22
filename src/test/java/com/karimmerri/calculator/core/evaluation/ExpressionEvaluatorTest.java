package com.karimmerri.calculator.core.evaluation;

import com.karimmerri.calculator.core.ast.BinaryExpression;
import com.karimmerri.calculator.core.ast.FunctionExpression;
import com.karimmerri.calculator.core.ast.NumberExpression;
import com.karimmerri.calculator.core.function.FunctionName;
import com.karimmerri.calculator.core.operator.BinaryOperator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionEvaluatorTest {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Test
    void shouldEvaluatePower() {
        assertThat(evaluator.evaluate(
                binary(number("2"), BinaryOperator.POWER, number("8"))
        )).isEqualByComparingTo("256");
    }

    @Test
    void shouldEvaluateNegativeExponent() {
        assertThat(evaluator.evaluate(
                binary(number("2"), BinaryOperator.POWER, number("-2"))
        )).isEqualByComparingTo("0.25");
    }

    @Test
    void shouldRejectFractionalExponent() {
        assertThatThrownBy(() -> evaluator.evaluate(
                binary(number("2"), BinaryOperator.POWER, number("0.5"))
        ))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Only integer exponents are supported");
    }

    @Test
    void shouldEvaluateSquareRoot() {
        assertThat(evaluator.evaluate(
                new FunctionExpression(FunctionName.SQRT, number("4"))
        )).isEqualByComparingTo("2");
    }

    @Test
    void shouldRejectSquareRootOfNegativeNumber() {
        assertThatThrownBy(() -> evaluator.evaluate(
                new FunctionExpression(FunctionName.SQRT, number("-1"))
        ))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Square root is undefined for negative numbers");
    }

    private static NumberExpression number(String value) {
        return new NumberExpression(new BigDecimal(value));
    }

    private static BinaryExpression binary(
            NumberExpression left,
            BinaryOperator operator,
            NumberExpression right
    ) {
        return new BinaryExpression(left, operator, right);
    }
}
