package com.karimmerri.calculator.core.expression;

import java.util.Objects;

public record UnaryExpression(
        UnaryOperator operator,
        Expression operand
) implements Expression {

    public UnaryExpression {
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(operand, "operand must not be null");
    }
}
