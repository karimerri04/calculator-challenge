package com.karimmerri.calculator.core.parser;

import com.karimmerri.calculator.core.expression.BinaryExpression;
import com.karimmerri.calculator.core.expression.BinaryOperator;
import com.karimmerri.calculator.core.expression.Expression;
import com.karimmerri.calculator.core.expression.FunctionExpression;
import com.karimmerri.calculator.core.expression.FunctionName;
import com.karimmerri.calculator.core.expression.NumberExpression;
import com.karimmerri.calculator.core.expression.UnaryExpression;
import com.karimmerri.calculator.core.expression.UnaryOperator;
import com.karimmerri.calculator.core.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParserTest {

    private final Lexer lexer = new Lexer();
    private final Parser parser = new Parser();

    @Test
    void shouldRespectMultiplicationPrecedence() {
        assertThat(parse("2+3*4"))
                .isEqualTo(binary(
                        number("2"),
                        BinaryOperator.ADD,
                        binary(number("3"), BinaryOperator.MULTIPLY, number("4"))
                ));
    }

    @Test
    void shouldParsePower() {
        assertThat(parse("2^8"))
                .isEqualTo(binary(number("2"), BinaryOperator.POWER, number("8")));
    }

    @Test
    void shouldParsePowerAsRightAssociative() {
        assertThat(parse("2^3^2"))
                .isEqualTo(binary(
                        number("2"),
                        BinaryOperator.POWER,
                        binary(number("3"), BinaryOperator.POWER, number("2"))
                ));
    }

    @Test
    void shouldGivePowerHigherPrecedenceThanUnaryMinus() {
        assertThat(parse("-2^2"))
                .isEqualTo(unary(
                        UnaryOperator.NEGATE,
                        binary(number("2"), BinaryOperator.POWER, number("2"))
                ));
    }

    @Test
    void shouldParseNegativeExponent() {
        assertThat(parse("2^-2"))
                .isEqualTo(binary(
                        number("2"),
                        BinaryOperator.POWER,
                        unary(UnaryOperator.NEGATE, number("2"))
                ));
    }

    @Test
    void shouldParseNestedParentheses() {
        assertThat(parse("(((1+2)))"))
                .isEqualTo(binary(number("1"), BinaryOperator.ADD, number("2")));
    }

    @Test
    void shouldParseSqrtFunction() {
        assertThat(parse("sqrt(4)"))
                .isEqualTo(new FunctionExpression(FunctionName.SQRT, number("4")));
    }

    @Test
    void shouldParseFunctionWithFullExpressionArgument() {
        assertThat(parse("sqrt(2+7)"))
                .isEqualTo(new FunctionExpression(
                        FunctionName.SQRT,
                        binary(number("2"), BinaryOperator.ADD, number("7"))
                ));
    }

    @Test
    void shouldRejectUnknownFunction() {
        assertThatThrownBy(() -> parse("foo(4)"))
                .isInstanceOf(ParserException.class)
                .hasMessage("Unknown function 'foo' at position 0");
    }

    @Test
    void shouldRequireFunctionParentheses() {
        assertThatThrownBy(() -> parse("sqrt 4"))
                .isInstanceOf(ParserException.class)
                .hasMessageContaining("Expected '('");
    }

    @Test
    void shouldRejectMissingClosingParenthesis() {
        assertThatThrownBy(() -> parse("(1+2"))
                .isInstanceOf(ParserException.class)
                .hasMessageContaining("Expected ')'");
    }

    @Test
    void shouldRejectUnexpectedTrailingToken() {
        assertThatThrownBy(() -> parse("1 2"))
                .isInstanceOf(ParserException.class)
                .hasMessageContaining("Unexpected token");
    }

    private Expression parse(String source) {
        return parser.parse(lexer.tokenize(source));
    }

    private static NumberExpression number(String value) {
        return new NumberExpression(new BigDecimal(value));
    }

    private static UnaryExpression unary(
            UnaryOperator operator,
            Expression operand
    ) {
        return new UnaryExpression(operator, operand);
    }

    private static BinaryExpression binary(
            Expression left,
            BinaryOperator operator,
            Expression right
    ) {
        return new BinaryExpression(left, operator, right);
    }
}
