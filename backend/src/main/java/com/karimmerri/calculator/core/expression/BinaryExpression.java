package com.karimmerri.calculator.core.expression;

import java.util.Objects;

public record BinaryExpression(
        Expression left,
        BinaryOperator operator,
        Expression right
) implements Expression {

    public BinaryExpression {
        Objects.requireNonNull(left, "left must not be null");
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(right, "right must not be null");
    }
}
