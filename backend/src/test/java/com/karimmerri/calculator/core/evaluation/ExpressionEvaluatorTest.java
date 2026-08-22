package com.karimmerri.calculator.core.evaluation;

import com.karimmerri.calculator.core.ast.BinaryExpression;
import com.karimmerri.calculator.core.ast.Expression;
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
    void shouldEvaluateBasicArithmetic() {
        assertThat(evaluate("2", BinaryOperator.ADD, "3"))
                .isEqualByComparingTo("5");
        assertThat(evaluate("7", BinaryOperator.SUBTRACT, "2"))
                .isEqualByComparingTo("5");
        assertThat(evaluate("3", BinaryOperator.MULTIPLY, "4"))
                .isEqualByComparingTo("12");
        assertThat(evaluate("10", BinaryOperator.DIVIDE, "4"))
                .isEqualByComparingTo("2.5");
    }

    @Test
    void shouldEvaluatePower() {
        assertThat(evaluate("2", BinaryOperator.POWER, "8"))
                .isEqualByComparingTo("256");
    }

    @Test
    void shouldEvaluateNegativeExponent() {
        assertThat(evaluate("2", BinaryOperator.POWER, "-2"))
                .isEqualByComparingTo("0.25");
    }

    @Test
    void shouldRejectFractionalExponent() {
        assertThatThrownBy(() -> evaluate("2", BinaryOperator.POWER, "0.5"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Only integer exponents are supported");
    }

    @Test
    void shouldRejectExponentAboveGuardRail() {
        assertThatThrownBy(() -> evaluate("2", BinaryOperator.POWER, "10001"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Exponent is outside the supported range [-10000, 10000]");
    }

    @Test
    void shouldRejectExponentOutsideIntegerRange() {
        assertThatThrownBy(() -> evaluate("2", BinaryOperator.POWER, "2147483648"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Exponent is outside the supported range [-10000, 10000]");
    }

    @Test
    void shouldRejectDivisionByZero() {
        assertThatThrownBy(() -> evaluate("1", BinaryOperator.DIVIDE, "0"))
                .isInstanceOf(CalculationException.class)
                .hasMessage("Division by zero is not allowed");
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

    private BigDecimal evaluate(
            String left,
            BinaryOperator operator,
            String right
    ) {
        return evaluator.evaluate(binary(number(left), operator, number(right)));
    }

    private static NumberExpression number(String value) {
        return new NumberExpression(new BigDecimal(value));
    }

    private static BinaryExpression binary(
            Expression left,
            BinaryOperator operator,
            Expression right
    ) {
        return new BinaryExpression(left, operator, right);
    }
}
