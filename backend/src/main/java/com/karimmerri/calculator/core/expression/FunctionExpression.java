package com.karimmerri.calculator.core.expression;

import java.util.Objects;

public record FunctionExpression(
        FunctionName function,
        Expression argument
) implements Expression {

    public FunctionExpression {
        Objects.requireNonNull(function, "function must not be null");
        Objects.requireNonNull(argument, "argument must not be null");
    }
}
